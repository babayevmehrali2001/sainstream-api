package saintstream_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import saintstream_api.docs.AuthApiDocs;
import saintstream_api.dto.ApiResponse;
import saintstream_api.dto.auth.*;
import saintstream_api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        return new ApiResponse<>(true, "Registration successful", authService.register(request));
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return new ApiResponse<>(true, "Login successful", authService.login(request));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Long userId) {
        return new ApiResponse<>(true, "OK", authService.getMe(userId));
    }

    @Override
    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return new ApiResponse<>(true, "Token refreshed", authService.refreshToken(request));
    }

    @Override
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return new ApiResponse<>(true, "Logged out successfully", null);
    }
}