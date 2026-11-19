package saintstream_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import saintstream_api.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Date;
import java.util.HexFormat;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration expiresIn;
    private final SecureRandom random = new SecureRandom();

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expires-in}") Duration expiresIn) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresIn = expiresIn;
    }


    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiresIn.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // token düzgündürsə, içindəki məlumatı qaytarır; deyilsə exception atır
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiresInSeconds() {
        return expiresIn.toSeconds();
    }


    public String generateRefreshToken() {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}