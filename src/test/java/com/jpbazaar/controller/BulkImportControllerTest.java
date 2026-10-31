package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.CategoryRequest;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BulkImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String customerToken;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail("admin_bulk_test@jpbazaar.com")) {
            userRepository.save(User.builder()
                    .firstName("Admin")
                    .lastName("Bulk")
                    .email("admin_bulk_test@jpbazaar.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build());
        }

        if (!userRepository.existsByEmail("cust_bulk_test@jpbazaar.com")) {
            userRepository.save(User.builder()
                    .firstName("Customer")
                    .lastName("Bulk")
                    .email("cust_bulk_test@jpbazaar.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .build());
        }

        Authentication adminAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin_bulk_test@jpbazaar.com", "Admin@123")
        );
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication customerAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("cust_bulk_test@jpbazaar.com", "Customer@123")
        );
        customerToken = jwtTokenProvider.generateToken(customerAuth);

        // Ensure test category exists
        if (categoryRepository.findByName("BulkDept").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("BulkDept")
                    .description("Test department for bulk import")
                    .build());
        }
    }

    @Test
    void downloadCsvTemplate_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/admin/products/import/template/csv")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"jpbazaar_products_template.csv\""))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Name,Category,Brand,SKU,Price")));
    }

    @Test
    void downloadExcelTemplate_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/admin/products/import/template/excel")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"jpbazaar_products_template.xlsx\""))
                .andExpect(content().contentTypeCompatibleWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    void previewImport_ValidCsv_Success() throws Exception {
        long ts = System.currentTimeMillis();
        String csvContent = "Name,Category,Brand,SKU,Price,OriginalPrice,Stock,Description,Specifications,ImageUrl\n" +
                "Pro Camera " + ts + ",BulkDept,Nikon,CAM-" + ts + ",1299.99,1499.99,15,Mirrorless 4K,{\"Sensor\":\"Full Frame\"},https://example.com/cam.jpg\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/admin/products/import/preview")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRows").value(1))
                .andExpect(jsonPath("$.data.validRows").value(1))
                .andExpect(jsonPath("$.data.invalidRows").value(0))
                .andExpect(jsonPath("$.data.hasErrors").value(false));
    }

    @Test
    void previewImport_WithInvalidCategoryAndDuplicateSku_ReportsDiagnostics() throws Exception {
        long ts = System.currentTimeMillis();
        String csvContent = "Name,Category,Brand,SKU,Price,OriginalPrice,Stock,Description,Specifications,ImageUrl\n" +
                "Item 1,NonExistentCategory,BrandA,DUP-" + ts + ",100.00,120.00,10,Desc,,https://example.com/1.jpg\n" +
                "Item 2,BulkDept,BrandB,DUP-" + ts + ",-50.00,120.00,-5,Desc,,\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "invalid_products.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/admin/products/import/preview")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRows").value(2))
                .andExpect(jsonPath("$.data.hasErrors").value(true))
                .andExpect(jsonPath("$.data.invalidRows").value(2))
                .andExpect(jsonPath("$.data.duplicateSkusCount").value(1));
    }

    @Test
    void executeImport_ValidCsv_PersistsToDatabase() throws Exception {
        long ts = System.currentTimeMillis();
        String sku = "BULK-TEST-" + ts;
        String csvContent = "Name,Category,Brand,SKU,Price,OriginalPrice,Stock,Description,Specifications,ImageUrl\n" +
                "Bulk Tested Product " + ts + ",BulkDept,Sony," + sku + ",299.99,349.99,25,High quality wireless sound,{\"Bluetooth\":\"5.3\"},https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "products_confirm.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/admin/products/import/confirm")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successfullyImported").value(1))
                .andExpect(jsonPath("$.data.failedRows").value(0));

        // Verify product is in database
        org.junit.jupiter.api.Assertions.assertTrue(productRepository.existsBySku(sku));
    }

    @Test
    void previewImport_AsCustomer_Forbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "Name,Category,Brand,SKU,Price,OriginalPrice,Stock,Description,Specifications,ImageUrl\n".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/admin/products/import/preview")
                        .file(file)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }
}
