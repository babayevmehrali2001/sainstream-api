package saintstream_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeroResponse {
    private Long id;
    private String title;
    private String type;
    private Integer releaseYear;
    private Integer durationMinutes;
    private Integer seasonCount;
    private List<String> genres;
    private String description;
    private String backdropUrl;
    private String trailerUrl;
}