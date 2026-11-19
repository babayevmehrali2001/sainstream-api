package saintstream_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR = "authError";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && !header.isBlank()) {
            if (!header.startsWith("Bearer ") || header.length() <= 7) {
                request.setAttribute(AUTH_ERROR, "Invalid token format");
            } else {
                try {
                    Claims claims = jwtService.parseAccessToken(header.substring(7));
                    Long userId = ((Number) claims.get("userId")).longValue();

                    // istifadəçini "giriş etmiş" kimi qeyd et
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userId, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (ExpiredJwtException e) {
                    request.setAttribute(AUTH_ERROR, "Token expired");
                } catch (JwtException | IllegalArgumentException e) {
                    request.setAttribute(AUTH_ERROR, "Invalid token");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}