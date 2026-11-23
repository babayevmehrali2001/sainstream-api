package saintstream_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @Schema(description = "Login və ya refresh cavabından gələn refresh token",
            example = "a3f9c1e07b...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}