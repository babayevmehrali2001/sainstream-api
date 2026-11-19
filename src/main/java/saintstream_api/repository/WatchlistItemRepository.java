package saintstream_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saintstream_api.model.WatchlistItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    long deleteByUserIdAndContentId(Long userId, Long contentId);

    @Query(value = "SELECT COUNT(*) FROM contents WHERE id = :contentId", nativeQuery = true)
    long countContent(@Param("contentId") Long contentId);

    // istifadəçinin siyahısı: film məlumatı + janrlar bir sorğuda (N+1 yoxdur)
    @Query(value = """
            SELECT c.id          AS id,
                   c.title       AS title,
                   c.poster_url  AS posterUrl,
                   c.type        AS type,
                   c.rating      AS rating,
                   c.age_rating  AS ageRating,
                   w.added_at    AS addedAt,
                   GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ',') AS genres
            FROM watchlist_items w
            JOIN contents c             ON c.id = w.content_id
            LEFT JOIN content_genres cg ON cg.content_id = c.id
            LEFT JOIN genres g          ON g.id = cg.genre_id
            WHERE w.user_id = :userId
            GROUP BY w.id, c.id
            ORDER BY w.added_at DESC, w.id DESC
            """, nativeQuery = true)
    List<WatchlistRow> findMyList(@Param("userId") Long userId);

    // native sorğunun nəticəsi üçün projection: hər getter bir sütun ləqəbinə uyğundur
    interface WatchlistRow {
        Long getId();
        String getTitle();
        String getPosterUrl();
        String getType();
        BigDecimal getRating();
        String getAgeRating();
        LocalDateTime getAddedAt();
        String getGenres();
    }
}