package com.jpbazaar.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Robust Database Configuration for JPBazaar.
 * Automatically parses cloud database connection strings (Render, Railway, Heroku, Supabase, Neon)
 * which are typically supplied in "postgresql://user:pass@host:port/database" URI format,
 * translating them into standard JDBC URLs accepted by {@link org.postgresql.Driver}.
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    public record ConnectionDetails(String jdbcUrl, String username, String password, String driverClassName) {}

    /**
     * Resolves connection details from either raw URI strings, JDBC URLs, or discrete host/database variables.
     */
    public static ConnectionDetails resolveConnection(
            String rawUrl,
            String username,
            String password,
            String configuredDriver,
            String dbHost,
            String dbPort,
            String dbName) {

        String finalJdbcUrl;
        String finalUsername = username;
        String finalPassword = password;
        String finalDriver = configuredDriver;

        if (StringUtils.hasText(rawUrl) && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            try {
                String uriString = rawUrl.startsWith("postgres://")
                        ? "postgresql://" + rawUrl.substring("postgres://".length())
                        : rawUrl;
                URI uri = URI.create(uriString);

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                if (path != null && path.startsWith("/")) {
                    path = path.substring(1);
                }

                if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    if (!StringUtils.hasText(finalUsername)) {
                        finalUsername = URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8);
                    }
                    if (userInfo.length > 1 && !StringUtils.hasText(finalPassword)) {
                        finalPassword = URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8);
                    }
                }

                StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                        .append(host)
                        .append(":")
                        .append(port)
                        .append("/")
                        .append(path);

                if (StringUtils.hasText(uri.getQuery())) {
                    sb.append("?").append(uri.getQuery());
                }

                finalJdbcUrl = sb.toString();
                log.info("Normalized Cloud PostgreSQL URI to standard JDBC URL: jdbc:postgresql://{}:{}/{}", host, port, path);
            } catch (Exception e) {
                log.warn("Failed to parse PostgreSQL URI: {}. Prepending 'jdbc:' as fallback.", e.getMessage());
                finalJdbcUrl = "jdbc:" + rawUrl;
            }
        } else if (StringUtils.hasText(rawUrl)) {
            finalJdbcUrl = rawUrl.startsWith("jdbc:") ? rawUrl : "jdbc:postgresql://" + rawUrl;
        } else if (StringUtils.hasText(dbHost) && StringUtils.hasText(dbName)) {
            int port = 5432;
            if (StringUtils.hasText(dbPort)) {
                try {
                    port = Integer.parseInt(dbPort.trim());
                } catch (NumberFormatException ignored) {
                    port = 5432;
                }
            }
            finalJdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", dbHost, port, dbName);
        } else {
            finalJdbcUrl = "jdbc:postgresql://localhost:5432/jpbazaar";
        }

        if (!StringUtils.hasText(finalDriver)) {
            if (finalJdbcUrl.contains(":h2:")) {
                finalDriver = "org.h2.Driver";
            } else {
                finalDriver = "org.postgresql.Driver";
            }
        }

        return new ConnectionDetails(finalJdbcUrl, finalUsername, finalPassword, finalDriver);
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${spring.datasource.url:${DB_URL:${DATABASE_URL:}}}") String rawUrl,
            @Value("${spring.datasource.username:${DB_USERNAME:}}") String username,
            @Value("${spring.datasource.password:${DB_PASSWORD:}}") String password,
            @Value("${spring.datasource.driver-class-name:}") String driverClassName,
            @Value("${DB_HOST:}") String dbHost,
            @Value("${DB_PORT:5432}") String dbPort,
            @Value("${DB_NAME:}") String dbName,
            @Value("${spring.datasource.hikari.maximum-pool-size:${HIKARI_MAX_POOL_SIZE:10}}") int maxPoolSize,
            @Value("${spring.datasource.hikari.minimum-idle:${HIKARI_MIN_IDLE:2}}") int minIdle,
            @Value("${spring.datasource.hikari.pool-name:JPBazaarHikariCP}") String poolName) {

        ConnectionDetails details = resolveConnection(
                rawUrl, username, password, driverClassName, dbHost, dbPort, dbName);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(details.jdbcUrl());
        hikariConfig.setUsername(details.username());
        hikariConfig.setPassword(details.password());
        hikariConfig.setDriverClassName(details.driverClassName());
        hikariConfig.setPoolName(poolName);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minIdle);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setConnectionTimeout(20000);
        hikariConfig.setLeakDetectionThreshold(60000);

        return new HikariDataSource(hikariConfig);
    }
}
