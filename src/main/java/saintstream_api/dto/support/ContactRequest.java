package saintstream_api.dto.support;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
//camelCase istediyi ucun bunu yazdim, diger tasklarda snake_case istemisdi
public class ContactRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneCountryCode;
    private String phoneNumber;
    private String message;
}
