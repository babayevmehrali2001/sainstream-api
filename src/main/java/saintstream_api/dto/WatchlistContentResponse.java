package saintstream_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistContentResponse {
    private Long id;
    private String title;
    private String posterUrl;
    private String type;
    private BigDecimal rating;
    private String ageRating;
    private List<String> genres;
    private LocalDateTime addedAt;
}