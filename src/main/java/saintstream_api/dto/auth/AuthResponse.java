package saintstream_api.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}