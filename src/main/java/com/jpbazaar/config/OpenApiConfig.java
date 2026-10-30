package com.jpbazaar.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration for JPBazaar API documentation.
 * Configures JWT Bearer authentication, API metadata, tag ordering, and security components.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI jpBazaarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JPBazaar E-Commerce REST API")
                        .description("""
                                ## Overview
                                Production-grade, secure REST API documentation for the **JPBazaar** E-Commerce platform.
                                Built with **Java 21**, **Spring Boot 3.3.4**, **PostgreSQL**, **Redis**, and **Spring Security**.
                                
                                ### Authentication
                                Secured endpoints require a valid JWT bearer token. Authenticate via `/api/auth/login` or `/api/auth/register`,
                                copy the `accessToken`, click **Authorize** at the top-right of this page, and paste the token.
                                
                                ### Architectural Pillars
                                - **Standardized Response Envelope**: All endpoints return `ApiResponse<T>` with `success`, `message`, `data`, and `timestamp`.
                                - **IDOR Defense**: All user actions strictly verify resource ownership against the authenticated token subject.
                                - **Concurrency & ACID Transactions**: Checkout utilizes pessimistic row locks (`SELECT ... FOR UPDATE`) and deadlock-free deterministic lock ordering.
                                - **Redis Read-Through Caching**: Product details and categories are cached with dynamic TTLs and automated mutation eviction.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("JPBazaar Development Team")
                                .email("dev@jpbazaar.com")
                                .url("https://github.com/jpbazaar"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/").description("Current Server Context")
                ))
                .tags(List.of(
                        new Tag().name("Authentication").description("User Registration, Login, and JWT Token issuance"),
                        new Tag().name("User Management").description("Customer profile inspection, updates, and password changes"),
                        new Tag().name("Address Book").description("Customer shipping address management and default address selection"),
                        new Tag().name("Categories").description("Product category hierarchy and admin catalog management"),
                        new Tag().name("Products").description("Product catalog search, multi-param filtering, pagination, and admin CRUD"),
                        new Tag().name("Product Images").description("Image gallery upload, storage management, and primary thumbnail assignment"),
                        new Tag().name("Cart").description("Shopping cart operations, quantity updates, duplicate auto-merging, and stock validation"),
                        new Tag().name("Wishlist").description("Customer product bookmarking and wishlist item management"),
                        new Tag().name("Orders").description("Atomic checkout, order tracking, cancellation lifecycle, and admin management"),
                        new Tag().name("Payments").description("Simulated payment gateway processing (Card, UPI, NetBanking, COD) and refunds"),
                        new Tag().name("Reviews").description("Verified-buyer ratings (1-5 stars) and review submissions with auto average calculation"),
                        new Tag().name("Admin Analytics").description("Administrative analytics dashboard, sales trends, KPIs, and inventory health metrics"),
                        new Tag().name("Media Storage").description("Public static media resource serving"),
                        new Tag().name("System Health").description("Service health check and diagnostic monitoring")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT access token obtained from /api/auth/login or /api/auth/register"))
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Alias for Bearer Authentication")));
    }
}
