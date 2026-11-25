package saintstream_api.dto.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ContactResponse {
    private Long id;
    private LocalDateTime createdAt;
}
