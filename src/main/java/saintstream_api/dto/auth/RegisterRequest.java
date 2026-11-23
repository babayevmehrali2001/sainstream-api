package saintstream_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterRequest {

    @Schema(description = "İstifadəçi adı", example = "mehrali",
            minLength = 3, maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Email ünvanı", example = "mehrali@test.com",
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Şifrə", example = "Password123",
            minLength = 8,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}