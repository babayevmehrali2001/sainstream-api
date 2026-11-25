package saintstream_api.dto.support;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqResponse {
    private Long id;
    private String question;
    private String answer;
    private Integer orderNumber;
}
