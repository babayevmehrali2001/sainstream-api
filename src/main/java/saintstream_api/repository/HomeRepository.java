package saintstream_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saintstream_api.model.Content;

import java.math.BigDecimal;
import java.util.List;

public interface HomeRepository extends JpaRepository<Content, Long> {

    @Query(value = """
            SELECT id, name, logo_url AS logoUrl
            FROM platforms
            WHERE is_active = TRUE
            ORDER BY order_number
            """, nativeQuery = true)
    List<PlatformRow> findActivePlatforms();

    @Query(value = """
            SELECT c.id AS id, c.title AS title, c.type AS type,
                   c.release_year AS releaseYear, c.duration_minutes AS durationMinutes,
                   c.season_count AS seasonCount, c.description AS description,
                   c.backdrop_url AS backdropUrl, c.trailer_url AS trailerUrl,
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
            """, nativeQuery = true)
    List<HeroRow> findHero();

    @Query(value = """
            SELECT c.id AS id, c.title AS title, ci.label AS tagline, c.rating AS rating,
                   c.duration_minutes AS durationMinutes, c.release_year AS releaseYear,
                   c.description AS description, c.poster_url AS posterUrl,
                   c.backdrop_url AS backdropUrl,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM collection_items ci
            JOIN collections col        ON col.id = ci.collection_id
            JOIN contents c             ON c.id   = ci.content_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id   = cg.genre_id
            WHERE col.slug = 'featured'
            GROUP BY ci.id, c.id
            ORDER BY ci.order_number
            """, nativeQuery = true)
    List<FeaturedRow> findFeatured();



    @Query(value = """
            SELECT c.id AS id, c.title AS title, c.poster_url AS posterUrl, c.type AS type,
                   c.rating AS rating, c.age_rating AS ageRating,
                   c.release_year AS releaseYear, c.season_count AS seasonCount,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM contents c
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id = cg.genre_id
            GROUP BY c.id
            ORDER BY c.release_date DESC, c.id
            LIMIT :limit
            """, nativeQuery = true)
    List<SectionRow> findLatest(@Param("limit") int limit);


    @Query(value = """
            SELECT c.id AS id, c.title AS title, c.poster_url AS posterUrl, c.type AS type,
                   c.rating AS rating, c.age_rating AS ageRating,
                   c.release_year AS releaseYear, c.season_count AS seasonCount,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM contents c
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id = cg.genre_id
            WHERE c.type = :type
            GROUP BY c.id
            ORDER BY c.rating DESC, c.id
            LIMIT :limit
            """, nativeQuery = true)
    List<SectionRow> findTopRatedByType(@Param("type") String type,
                                        @Param("limit") int limit);


    @Query(value = """
            SELECT c.id AS id, c.title AS title, c.poster_url AS posterUrl, c.type AS type,
                   c.rating AS rating, c.age_rating AS ageRating,
                   c.release_year AS releaseYear, c.season_count AS seasonCount,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM contents c
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id = cg.genre_id
            WHERE c.type = :type AND c.origin_country = :country
            GROUP BY c.id
            ORDER BY c.rating DESC, c.id
            LIMIT :limit
            """, nativeQuery = true)
    List<SectionRow> findTopRatedByTypeAndCountry(@Param("type") String type,
                                                  @Param("country") String country,
                                                  @Param("limit") int limit);


    @Query(value = """
            SELECT c.id AS id, c.title AS title, c.poster_url AS posterUrl, c.type AS type,
                   c.rating AS rating, c.age_rating AS ageRating,
                   c.release_year AS releaseYear, c.season_count AS seasonCount,
                   c.description AS description, c.backdrop_url AS backdropUrl,
                   c.duration_minutes AS durationMinutes,
                   ci.order_number AS orderNumber, ci.label AS label,
                   p.name AS platformName, p.logo_url AS platformLogoUrl,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM collection_items ci
            JOIN collections col        ON col.id = ci.collection_id
            JOIN contents c             ON c.id   = ci.content_id
            LEFT JOIN platforms p       ON p.id   = c.platform_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id   = cg.genre_id
            WHERE col.slug = :slug
            GROUP BY ci.id, c.id, p.id
            ORDER BY ci.order_number
            LIMIT :limit
            """, nativeQuery = true)
    List<CollectionSectionRow> findSectionFromCollection(@Param("slug") String slug,
                                                         @Param("limit") int limit);

    // ==================== PROJECTIONS ====================
    // hər getter SQL-dəki bir AS ləqəbinə uyğundur (logoUrl → getLogoUrl)

    interface PlatformRow {
        Long getId();
        String getName();
        String getLogoUrl();
    }

    interface HeroRow {
        Long getId();
        String getTitle();
        String getType();
        Integer getReleaseYear();
        Integer getDurationMinutes();
        Integer getSeasonCount();
        String getDescription();
        String getBackdropUrl();
        String getTrailerUrl();
        String getGenres();
    }

    interface FeaturedRow {
        Long getId();
        String getTitle();
        String getTagline();
        BigDecimal getRating();
        Integer getDurationMinutes();
        Integer getReleaseYear();
        String getDescription();
        String getPosterUrl();
        String getBackdropUrl();
        String getGenres();
    }

    interface SectionRow {
        Long getId();
        String getTitle();
        String getPosterUrl();
        String getType();
        BigDecimal getRating();
        String getAgeRating();
        Integer getReleaseYear();
        Integer getSeasonCount();
        String getGenres();
    }

    // SectionRow-un bütün sahələri + kolleksiyaya aid əlavələr
    interface CollectionSectionRow extends SectionRow {
        Integer getOrderNumber();
        String getLabel();
        String getDescription();
        String getBackdropUrl();
        Integer getDurationMinutes();
        String getPlatformName();
        String getPlatformLogoUrl();
    }
}