package com.jpbazaar.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test verifying OpenAPI 3.0 / Swagger UI documentation generation,
 * metadata, security scheme declarations, tags, and endpoint exposures.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should serve OpenAPI 3 specification at /v3/api-docs with valid metadata and security schemes")
    void shouldServeOpenApiSpecWithValidMetadataAndSecurity() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi", startsWith("3.")))
                .andExpect(jsonPath("$.info.title").value("JPBazaar E-Commerce REST API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.info.contact.name").value("JPBazaar Development Team"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].bearerFormat").value("JWT"))
                .andExpect(jsonPath("$.components.securitySchemes['bearerAuth']").exists());
    }

    @Test
    @DisplayName("Should define all required domain tags in OpenAPI specification")
    void shouldContainAllDomainTags() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags[*].name", hasItems(
                        "Authentication",
                        "User Management",
                        "Address Book",
                        "Categories",
                        "Products",
                        "Product Images",
                        "Cart",
                        "Wishlist",
                        "Orders",
                        "Payments",
                        "Reviews",
                        "Admin Analytics",
                        "Media Storage",
                        "System Health"
                )));
    }

    @Test
    @DisplayName("Should expose key API endpoints in OpenAPI paths")
    void shouldExposeKeyEndpointsInOpenApiPaths() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/auth/register']").exists())
                .andExpect(jsonPath("$.paths['/api/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/api/products']").exists())
                .andExpect(jsonPath("$.paths['/api/categories']").exists())
                .andExpect(jsonPath("$.paths['/api/cart']").exists())
                .andExpect(jsonPath("$.paths['/api/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/payments/process']").exists())
                .andExpect(jsonPath("$.paths['/api/reviews']").exists())
                .andExpect(jsonPath("$.paths['/api/admin/dashboard/stats']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/health']").exists());
    }

    @Test
    @DisplayName("Should serve Swagger UI documentation endpoint")
    void shouldServeSwaggerUiEndpoint() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
