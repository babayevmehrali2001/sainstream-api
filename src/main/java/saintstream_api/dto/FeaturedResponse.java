package saintstream_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedResponse {
    private Long id;
    private String title;
    private String tagline;
    private BigDecimal rating;
    private Integer durationMinutes;
    private Integer releaseYear;
    private List<String> genres;
    private String description;
    private String posterUrl;
    private String backdropUrl;
}