package saintstream_api.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionCheck {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void checkConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("Database connection OK");
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Database connection failed. Check DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD in .env", e);
        }
    }
}