package saintstream_api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import saintstream_api.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HomeRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<PlatformResponse> findActivePlatforms() {
        String sql = """
                SELECT id, name, logo_url
                FROM platforms
                WHERE is_active = TRUE
                ORDER BY order_number
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PlatformResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("logo_url")
        ));
    }


    public List<HeroResponse> findHero() {
        String sql = """
            SELECT c.id, c.title, c.type, c.release_year, c.duration_minutes, c.season_count,
                   c.description, c.backdrop_url, c.trailer_url,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM collection_items ci
            JOIN collections col        ON col.id = ci.collection_id
            JOIN contents c             ON c.id   = ci.content_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id   = cg.genre_id
            WHERE col.slug = 'hero'
            GROUP BY ci.id, c.id
            ORDER BY ci.order_number
            LIMIT 5
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new HeroResponse(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("type"),
                rs.getObject("release_year", Integer.class),
                rs.getObject("duration_minutes", Integer.class),
                rs.getObject("season_count", Integer.class),
                toList(rs.getString("genres")),
                rs.getString("description"),
                rs.getString("backdrop_url"),
                rs.getString("trailer_url")
        ));
    }

    private List<String> toList(String genres) {
        if (genres == null) {
            return List.of();
        }
        return List.of(genres.split(","));
    }

    public List<FeaturedResponse> findFeatured() {
        String sql = """
            SELECT c.id, c.title, ci.label AS tagline, c.rating, c.duration_minutes, c.release_year,
                   c.description, c.poster_url, c.backdrop_url,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM collection_items ci
            JOIN collections col        ON col.id = ci.collection_id
            JOIN contents c             ON c.id   = ci.content_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id   = cg.genre_id
            WHERE col.slug = 'featured'
            GROUP BY ci.id, c.id
            ORDER BY ci.order_number
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FeaturedResponse(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("tagline"),
                rs.getBigDecimal("rating"),
                rs.getObject("duration_minutes", Integer.class),
                rs.getObject("release_year", Integer.class),
                toList(rs.getString("genres")),
                rs.getString("description"),
                rs.getString("poster_url"),
                rs.getString("backdrop_url")
        ));
    }




    // ==================== SECTIONS ====================

    // contents cədvəlindən: just-release, movies, series, korean-series
    public List<SectionItemResponse> findSectionFromContents(String where, String orderBy,
                                                             List<Object> params, int limit) {
        String sql = """
            SELECT c.id, c.title, c.poster_url, c.type, c.rating, c.age_rating,
                   c.release_year, c.season_count,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM contents c
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id = cg.genre_id
            WHERE %s
            GROUP BY c.id
            ORDER BY %s
            LIMIT ?
            """.formatted(where, orderBy);

        List<Object> allParams = new ArrayList<>(params);
        allParams.add(limit);

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapBase(rs), allParams.toArray());
    }

    // kolleksiyadan: popular-week, awards, fast, live
    public List<SectionItemResponse> findSectionFromCollection(String slug, int limit) {
        String sql = """
            SELECT c.id, c.title, c.poster_url, c.type, c.rating, c.age_rating,
                   c.release_year, c.season_count,
                   c.description, c.backdrop_url, c.duration_minutes,
                   ci.order_number, ci.label,
                   p.name AS platform_name, p.logo_url AS platform_logo_url,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM collection_items ci
            JOIN collections col        ON col.id = ci.collection_id
            JOIN contents c             ON c.id   = ci.content_id
            LEFT JOIN platforms p       ON p.id   = c.platform_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id   = cg.genre_id
            WHERE col.slug = ?
            GROUP BY ci.id, c.id, p.id
            ORDER BY ci.order_number
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SectionItemResponse item = mapBase(rs);

            if (slug.equals("popular-week")) {
                item.setRank(rs.getInt("order_number"));
            }

            if (slug.equals("awards")) {
                item.setLabel(rs.getString("label"));
                item.setDescription(rs.getString("description"));
                item.setBackdropUrl(rs.getString("backdrop_url"));
                item.setDurationMinutes(rs.getObject("duration_minutes", Integer.class));
                if (rs.getString("platform_name") != null) {
                    item.setPlatform(new PlatformInfo(
                            rs.getString("platform_name"),
                            rs.getString("platform_logo_url")));
                }
            }
            return item;
        }, slug, limit);
    }

    // bütün bölmələrdə olan ümumi sahələr
    private SectionItemResponse mapBase(ResultSet rs) throws SQLException {
        SectionItemResponse item = new SectionItemResponse();
        item.setId(rs.getLong("id"));
        item.setTitle(rs.getString("title"));
        item.setPosterUrl(rs.getString("poster_url"));
        item.setType(rs.getString("type"));
        item.setRating(rs.getBigDecimal("rating"));
        item.setAgeRating(rs.getString("age_rating"));
        item.setReleaseYear(rs.getObject("release_year", Integer.class));
        item.setSeasonCount(rs.getObject("season_count", Integer.class));
        item.setGenres(toList(rs.getString("genres")));
        return item;
    }


}