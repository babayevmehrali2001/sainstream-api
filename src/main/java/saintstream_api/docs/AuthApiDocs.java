package saintstream_api.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import saintstream_api.dto.auth.AuthResponse;
import saintstream_api.dto.auth.LoginRequest;
import saintstream_api.dto.auth.RefreshTokenRequest;
import saintstream_api.dto.auth.RegisterRequest;
import saintstream_api.dto.auth.UserResponse;

/**
 * AuthController-in Swagger sənədləşməsi.
 * Annotasiyalar burada saxlanılır ki, controller qısa və oxunaqlı qalsın.
 *
 * Diqqət: Swagger-in @ApiResponse annotasiyası ilə bizim saintstream_api.dto.ApiResponse
 * class-ının adı eynidir. Ona görə bizim class burada tam adı ilə yazılıb.
 */
@Tag(name = "Auth", description = "Qeydiyyat, giriş, token yeniləmə və çıxış")
public interface AuthApiDocs {

    // ==================== REGISTER ====================
    @Operation(
            summary = "Qeydiyyat",
            description = "Yeni istifadəçi yaradır və dərhal access_token + refresh_token qaytarır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "İstifadəçi yaradıldı",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                              "success": true,
                              "message": "Registration successful",
                              "data": {
                                "user": {
                                  "id": 1,
                                  "username": "mehrali",
                                  "email": "mehrali@test.com",
                                  "created_at": "2026-11-23T10:00:00"
                                },
                                "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.abc123",
                                "refresh_token": "a3f9c1e07b4d2e8f9a1b3c5d7e9f0a2b4c6d8e0f1a3b5c7d9e1f3a5b7c9d1e3f",
                                "expires_in": 900
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Qısa username", value = """
                                    { "success": false, "message": "Username must be between 3 and 20 characters", "data": null }
                                    """),
                            @ExampleObject(name = "Səhv email", value = """
                                    { "success": false, "message": "Invalid email format", "data": null }
                                    """),
                            @ExampleObject(name = "Qısa şifrə", value = """
                                    { "success": false, "message": "Password must be at least 8 characters", "data": null }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "Email və ya username artıq mövcuddur",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Email mövcuddur", value = """
                                    { "success": false, "message": "Email already in use", "data": null }
                                    """),
                            @ExampleObject(name = "Username mövcuddur", value = """
                                    { "success": false, "message": "Username already taken", "data": null }
                                    """)
                    }))
    })
    saintstream_api.dto.ApiResponse<AuthResponse> register(RegisterRequest request);

    // ==================== LOGIN ====================
    @Operation(
            summary = "Giriş",
            description = "Email və şifrə ilə giriş. Email tapılmadıqda da 404 yox, 401 qaytarılır: "
                    + "təhlükəsizlik səbəbindən hansı email-in qeydiyyatda olduğu açıqlanmır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uğurlu giriş",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                              "success": true,
                              "message": "Login successful",
                              "data": {
                                "user": {
                                  "id": 1,
                                  "username": "mehrali",
                                  "email": "mehrali@test.com",
                                  "created_at": "2026-11-23T10:00:00"
                                },
                                "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.abc123",
                                "refresh_token": "a3f9c1e07b4d2e8f9a1b3c5d7e9f0a2b4c6d8e0f1a3b5c7d9e1f3a5b7c9d1e3f",
                                "expires_in": 900
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Email və ya şifrə göndərilməyib",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Email yoxdur", value = """
                                    { "success": false, "message": "Email is required", "data": null }
                                    """),
                            @ExampleObject(name = "Şifrə yoxdur", value = """
                                    { "success": false, "message": "Password is required", "data": null }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "Email və ya şifrə yanlışdır",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            { "success": false, "message": "Invalid email or password", "data": null }
                            """))),
            @ApiResponse(responseCode = "403", description = "Hesab deaktiv edilib",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            { "success": false, "message": "Account is deactivated", "data": null }
                            """)))
    })
    saintstream_api.dto.ApiResponse<AuthResponse> login(LoginRequest request);

    // ==================== ME ====================
    @Operation(
            summary = "Cari istifadəçi",
            description = "Qorunan endpoint. Header: Authorization: Bearer {access_token}"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cari istifadəçinin məlumatı",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                              "success": true,
                              "message": "OK",
                              "data": {
                                "id": 1,
                                "username": "mehrali",
                                "email": "mehrali@test.com",
                                "created_at": "2026-11-23T10:00:00"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Token problemi",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Token yoxdur", value = """
                                    { "success": false, "message": "No token provided", "data": null }
                                    """),
                            @ExampleObject(name = "Etibarsız token", value = """
                                    { "success": false, "message": "Invalid token", "data": null }
                                    """),
                            @ExampleObject(name = "Vaxtı bitmiş token", value = """
                                    { "success": false, "message": "Token expired", "data": null }
                                    """)
                    }))
    })
    saintstream_api.dto.ApiResponse<UserResponse> me(@Parameter(hidden = true) Long userId);

    // ==================== REFRESH TOKEN ====================
    @Operation(
            summary = "Token yeniləmə",
            description = "Access token-in vaxtı bitəndə refresh_token ilə yeni token cütü alınır. "
                    + "Köhnə refresh token ləğv edilir (rotation), ona görə cavabdakı yeni refresh_token saxlanılmalıdır."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Yeni token cütü",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                              "success": true,
                              "message": "Token refreshed",
                              "data": {
                                "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xyz789",
                                "refresh_token": "b7d2e9f1a3c5e7f9b1d3f5a7c9e1b3d5f7a9c1e3b5d7f9a1c3e5b7d9f1a3c5e7",
                                "expires_in": 900
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "refresh_token göndərilməyib",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            { "success": false, "message": "Refresh token is required", "data": null }
                            """))),
            @ApiResponse(responseCode = "401", description = "Refresh token etibarsızdır və ya vaxtı bitib",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Etibarsız", value = """
                                    { "success": false, "message": "Invalid refresh token", "data": null }
                                    """),
                            @ExampleObject(name = "Vaxtı bitib", value = """
                                    { "success": false, "message": "Refresh token expired", "data": null }
                                    """)
                    }))
    })
    saintstream_api.dto.ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request);

    // ==================== LOGOUT ====================
    @Operation(
            summary = "Çıxış",
            description = """
                    Refresh token bazadan silinir və artıq istifadə edilə bilməz.

                    **Diqqət:** access token server tərəfdən ləğv edilmir. O, bazada saxlanılmır, \
                    yalnız imzası yoxlanılır, ona görə vaxtı bitənə qədər (15 dəqiqə) etibarlı qalır. \
                    Frontend logout zamanı access_token və refresh_token-i öz yaddaşından silməlidir.

                    Token tapılmasa belə cavab 200-dür: çıxış həmişə uğurlu sayılır."""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uğurlu çıxış",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            { "success": true, "message": "Logged out successfully", "data": null }
                            """)))
    })
    saintstream_api.dto.ApiResponse<Void> logout(RefreshTokenRequest request);
}
