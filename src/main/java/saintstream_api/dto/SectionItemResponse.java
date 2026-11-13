package saintstream_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class SectionItemResponse {
    // bütün bölmələrdə olan sahələr
    private Long id;
    private String title;
    private String posterUrl;
    private String type;
    private BigDecimal rating;
    private String ageRating;
    private Integer releaseYear;
    private List<String> genres;
    private Integer seasonCount;

    // yalnız popular-week
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer rank;

    // yalnız awards
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String label;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String backdropUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer durationMinutes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PlatformInfo platform;
}