package com.jpbazaar.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigTest {

    @Test
    @DisplayName("Should parse Render internal postgresql:// URL correctly into JDBC URL and credentials")
    void testParseRenderPostgreSqlUrl() {
        String renderUrl = "postgresql://jpbazaar_user:mVoz7riruMYlTh7lRpy7OI0gg2qvCz3c@dpg-dbjl72e7bikc73fq7hmg-a/jpbazaar";
        
        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                renderUrl, "", "", "", "", "5432", "");

        assertThat(details.jdbcUrl()).isEqualTo("jdbc:postgresql://dpg-dbjl72e7bikc73fq7hmg-a:5432/jpbazaar");
        assertThat(details.username()).isEqualTo("jpbazaar_user");
        assertThat(details.password()).isEqualTo("mVoz7riruMYlTh7lRpy7OI0gg2qvCz3c");
        assertThat(details.driverClassName()).isEqualTo("org.postgresql.Driver");
    }

    @Test
    @DisplayName("Should parse postgres:// URL with custom port and query parameters")
    void testParsePostgresWithPortAndQuery() {
        String url = "postgres://admin:secret123@db.example.com:5433/proddb?sslmode=require";

        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                url, "", "", "", "", "5432", "");

        assertThat(details.jdbcUrl()).isEqualTo("jdbc:postgresql://db.example.com:5433/proddb?sslmode=require");
        assertThat(details.username()).isEqualTo("admin");
        assertThat(details.password()).isEqualTo("secret123");
        assertThat(details.driverClassName()).isEqualTo("org.postgresql.Driver");
    }

    @Test
    @DisplayName("Should decode URL-encoded username and password")
    void testUrlEncodedCredentials() {
        String url = "postgresql://my%20user:p%40ss%23word@localhost:5432/testdb";

        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                url, "", "", "", "", "5432", "");

        assertThat(details.jdbcUrl()).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
        assertThat(details.username()).isEqualTo("my user");
        assertThat(details.password()).isEqualTo("p@ss#word");
    }

    @Test
    @DisplayName("Should keep existing jdbc:postgresql:// URL unchanged")
    void testStandardJdbcUrl() {
        String url = "jdbc:postgresql://localhost:5432/jpbazaar";

        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                url, "postgres", "postgres", "", "", "5432", "");

        assertThat(details.jdbcUrl()).isEqualTo("jdbc:postgresql://localhost:5432/jpbazaar");
        assertThat(details.username()).isEqualTo("postgres");
        assertThat(details.password()).isEqualTo("postgres");
    }

    @Test
    @DisplayName("Should construct JDBC URL from discrete DB_HOST and DB_NAME")
    void testDiscreteHostAndName() {
        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                "", "appuser", "apppass", "", "postgres.internal", "5432", "jpbazaar_db");

        assertThat(details.jdbcUrl()).isEqualTo("jdbc:postgresql://postgres.internal:5432/jpbazaar_db");
        assertThat(details.username()).isEqualTo("appuser");
        assertThat(details.password()).isEqualTo("apppass");
    }

    @Test
    @DisplayName("Should detect H2 driver for in-memory H2 URLs")
    void testH2DriverDetection() {
        String url = "jdbc:h2:mem:testdb;MODE=PostgreSQL";

        DatabaseConfig.ConnectionDetails details = DatabaseConfig.resolveConnection(
                url, "sa", "", "", "", "5432", "");

        assertThat(details.driverClassName()).isEqualTo("org.h2.Driver");
    }
}
