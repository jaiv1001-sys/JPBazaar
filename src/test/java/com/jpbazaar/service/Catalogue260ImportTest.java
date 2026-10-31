package com.jpbazaar.service;

import com.jpbazaar.dto.bulk.BulkImportPreviewResponse;
import com.jpbazaar.dto.bulk.BulkImportResultResponse;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductImageRepository;
import com.jpbazaar.repository.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class Catalogue260ImportTest {

    @Autowired
    private BulkImportService bulkImportService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private static final String CSV_PATH = "data/catalogue_260_products.csv";
    private static final String XLSX_PATH = "data/catalogue_260_products.xlsx";

    private static final String[] CATEGORIES = {
            "Electronics",
            "Fashion",
            "Home & Living",
            "Accessories",
            "Beauty",
            "Sports & Fitness"
    };

    @BeforeEach
    void setUp() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Seed the 6 catalogue departments
        for (String catName : CATEGORIES) {
            categoryRepository.save(Category.builder()
                    .name(catName)
                    .description("Department: " + catName)
                    .build());
        }
    }

    @Test
    @DisplayName("Convert CSV to Excel XLSX catalogue and verify structure")
    void generateExcelFromCsvIfMissing() throws Exception {
        Path csvFile = Paths.get(CSV_PATH);
        assertThat(Files.exists(csvFile)).as("CSV file should exist at " + CSV_PATH).isTrue();

        Path xlsxFile = Paths.get(XLSX_PATH);

        // Convert CSV to XLSX
        try (Reader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build());
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("260 Product Catalogue");

            // Style headers
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create header row
            Row headerRow = sheet.createRow(0);
            List<String> headers = csvParser.getHeaderNames();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (CSVRecord record : csvParser) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    String val = record.get(i);
                    // Check if numeric for price / originalPrice / stock
                    String headerName = headers.get(i).toLowerCase();
                    if ((headerName.equals("price") || headerName.equals("originalprice") || headerName.equals("stock"))
                            && val != null && !val.isBlank()) {
                        try {
                            double num = Double.parseDouble(val);
                            cell.setCellValue(num);
                        } catch (NumberFormatException e) {
                            cell.setCellValue(val);
                        }
                    } else {
                        cell.setCellValue(val != null ? val : "");
                    }
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (OutputStream out = Files.newOutputStream(xlsxFile)) {
                workbook.write(out);
            }
        }

        assertThat(Files.exists(xlsxFile)).isTrue();
        assertThat(Files.size(xlsxFile)).isGreaterThan(10000L);
    }

    @Test
    @DisplayName("Preview 260-product CSV and XLSX with 0 errors")
    void testPreviewCsvAndXlsxCatalogue() throws Exception {
        // Ensure XLSX exists
        generateExcelFromCsvIfMissing();

        // 1. Preview CSV
        byte[] csvBytes = Files.readAllBytes(Paths.get(CSV_PATH));
        MockMultipartFile csvFile = new MockMultipartFile("file", "catalogue_260_products.csv", "text/csv", csvBytes);

        BulkImportPreviewResponse csvPreview = bulkImportService.previewImport(csvFile);
        assertThat(csvPreview.totalRows()).isEqualTo(260);
        assertThat(csvPreview.validRows()).isEqualTo(260);
        assertThat(csvPreview.invalidRows()).isEqualTo(0);
        assertThat(csvPreview.duplicateSkusCount()).isEqualTo(0);
        assertThat(csvPreview.hasErrors()).isFalse();

        // 2. Preview XLSX
        byte[] xlsxBytes = Files.readAllBytes(Paths.get(XLSX_PATH));
        MockMultipartFile xlsxFile = new MockMultipartFile("file", "catalogue_260_products.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxBytes);

        BulkImportPreviewResponse xlsxPreview = bulkImportService.previewImport(xlsxFile);
        assertThat(xlsxPreview.totalRows()).isEqualTo(260);
        assertThat(xlsxPreview.validRows()).isEqualTo(260);
        assertThat(xlsxPreview.invalidRows()).isEqualTo(0);
        assertThat(xlsxPreview.duplicateSkusCount()).isEqualTo(0);
        assertThat(xlsxPreview.hasErrors()).isFalse();
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    @DisplayName("Execute full import of 260 products into database and verify departmental breakdown")
    void testExecuteImport260Catalogue() throws Exception {
        byte[] csvBytes = Files.readAllBytes(Paths.get(CSV_PATH));
        MockMultipartFile csvFile = new MockMultipartFile("file", "catalogue_260_products.csv", "text/csv", csvBytes);

        BulkImportResultResponse result = bulkImportService.executeImport(csvFile);

        assertThat(result.totalProcessed()).isEqualTo(260);
        assertThat(result.successfullyImported()).isEqualTo(260);
        assertThat(result.failedRows()).isEqualTo(0);
        assertThat(result.failureDetails()).isEmpty();
        assertThat(result.importedProductIds()).hasSize(260);

        // Verify total in database
        long totalProducts = productRepository.count();
        assertThat(totalProducts).isEqualTo(260);

        // Verify departmental counts
        Map<String, Long> expectedCounts = Map.of(
                "Electronics", 50L,
                "Fashion", 50L,
                "Home & Living", 40L,
                "Accessories", 40L,
                "Beauty", 40L,
                "Sports & Fitness", 40L
        );

        for (Map.Entry<String, Long> entry : expectedCounts.entrySet()) {
            Category cat = categoryRepository.findByName(entry.getKey()).orElseThrow();
            long count = productRepository.countByCategoryId(cat.getId());
            assertThat(count)
                    .as("Category " + entry.getKey() + " must have " + entry.getValue() + " products")
                    .isEqualTo(entry.getValue());
        }

        // Verify sample product details
        Product iphone = productRepository.findBySku("ELEC-APPL-001").orElseThrow();
        assertThat(iphone.getName()).contains("Apple iPhone 15 Pro Max");
        assertThat(iphone.getBrand()).isEqualTo("Apple");
        assertThat(iphone.getCategory().getName()).isEqualTo("Electronics");
        assertThat(iphone.getOriginalPrice()).isNotNull();
        assertThat(iphone.getOriginalPrice()).isGreaterThan(iphone.getPrice());
        assertThat(iphone.getSpecifications()).isNotBlank();
        assertThat(iphone.getSpecifications()).contains("Display");

        // Verify image attached
        var images = productImageRepository.findByProductId(iphone.getId());
        assertThat(images).hasSize(1);
        assertThat(images.get(0).getIsPrimary()).isTrue();
        assertThat(images.get(0).getImageUrl()).contains("images.unsplash.com");
    }
}
