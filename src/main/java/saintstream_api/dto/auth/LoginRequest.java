package saintstream_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(example = "mehrali@test.com", format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "Password123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}