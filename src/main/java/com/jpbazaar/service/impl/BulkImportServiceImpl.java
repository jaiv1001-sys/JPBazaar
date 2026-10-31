package com.jpbazaar.service.impl;

import com.jpbazaar.dto.bulk.BulkImportPreviewResponse;
import com.jpbazaar.dto.bulk.BulkImportResultResponse;
import com.jpbazaar.dto.bulk.ProductImportRowDto;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.ProductImage;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductImageRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.service.BulkImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkImportServiceImpl implements BulkImportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    private static final String[] HEADERS = {
            "Name", "Category", "Brand", "SKU", "Price",
            "OriginalPrice", "Stock", "Description", "Specifications", "ImageUrl"
    };

    private static final List<String[]> SAMPLE_DATA = List.of(
            new String[]{
                    "Apple iPhone 15 Pro Max 256GB", "Electronics", "Apple", "ELEC-APPL-001",
                    "134900.00", "159900.00", "45",
                    "A17 Pro chip, Titanium design with 48MP camera system",
                    "{\"Display\": \"6.7 inch Super Retina XDR\", \"Processor\": \"A17 Pro\", \"Storage\": \"256GB\"}",
                    "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800"
            },
            new String[]{
                    "Sony WH-1000XM5 Wireless Headphones", "Electronics", "Sony", "ELEC-SONY-002",
                    "29990.00", "34990.00", "60",
                    "Industry-leading noise cancelling with Auto NC Optimizer",
                    "{\"Battery\": \"30h with quick charge\", \"Weight\": \"250g\", \"Connectivity\": \"Bluetooth 5.2\"}",
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800"
            },
            new String[]{
                    "Nike Air Zoom Pegasus 40", "Fashion", "Nike", "FASH-NIKE-001",
                    "9495.00", "11995.00", "80",
                    "Responsive road running shoes with dual Zoom Air units",
                    "{\"Upper\": \"Engineered Mesh\", \"Cushioning\": \"Nike React foam\", \"Closure\": \"Lace-Up\"}",
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800"
            },
            new String[]{
                    "Philips Smart Wi-Fi LED Ceiling Lamp", "Home & Living", "Philips", "HOME-PHIL-001",
                    "3499.00", "4999.00", "35",
                    "Tunable white and color smart ceiling light compatible with Alexa and Google",
                    "{\"Wattage\": \"36W\", \"Connectivity\": \"Wi-Fi + BLE\", \"Lumen\": \"2800 lm\"}",
                    "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800"
            }
    );

    @Override
    public byte[] generateCsvTemplate() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {

            for (String[] row : SAMPLE_DATA) {
                printer.printRecord((Object[]) row);
            }
            printer.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate CSV product template", e);
            throw new RuntimeException("Could not generate CSV template", e);
        }
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Product Import Template");

            // Style for header row: Bold, Dark Background (#161616), Light Gold Font (#D4AF37)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.GOLD.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.LEFT);

            // Data row style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create Sample Data Rows
            int rowIdx = 1;
            for (String[] sample : SAMPLE_DATA) {
                Row row = sheet.createRow(rowIdx++);
                for (int col = 0; col < sample.length; col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellValue(sample[col]);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-fit column widths
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Ensure padding
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1024, 3500));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Excel product template", e);
            throw new RuntimeException("Could not generate Excel template", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BulkImportPreviewResponse previewImport(MultipartFile file) {
        validateFileMetadata(file);
        List<ProductImportRowDto> parsedRows = parseAndValidateFile(file);

        int totalRows = parsedRows.size();
        int validRows = (int) parsedRows.stream().filter(ProductImportRowDto::valid).count();
        int invalidRows = totalRows - validRows;
        int duplicateSkus = (int) parsedRows.stream()
                .filter(r -> r.errors().stream().anyMatch(e -> e.contains("Duplicate SKU") || e.contains("already exists")))
                .count();

        return new BulkImportPreviewResponse(
                totalRows,
                validRows,
                invalidRows,
                duplicateSkus,
                invalidRows > 0,
                parsedRows
        );
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "brands", allEntries = true)
    })
    public BulkImportResultResponse executeImport(MultipartFile file) {
        validateFileMetadata(file);
        List<ProductImportRowDto> parsedRows = parseAndValidateFile(file);

        Map<String, Category> categoryMap = loadCategoryMap();
        List<Long> importedIds = new ArrayList<>();
        List<ProductImportRowDto> failedRows = new ArrayList<>();

        for (ProductImportRowDto row : parsedRows) {
            if (!row.valid()) {
                failedRows.add(row);
                continue;
            }

            try {
                Category category = categoryMap.get(row.categoryName().trim().toLowerCase());
                if (category == null) {
                    List<String> errs = new ArrayList<>(row.errors());
                    errs.add("Category '" + row.categoryName() + "' was not found.");
                    failedRows.add(new ProductImportRowDto(
                            row.rowNumber(), row.name(), row.categoryName(), row.brand(), row.sku(),
                            row.price(), row.originalPrice(), row.stockQuantity(), row.description(),
                            row.specifications(), row.imageUrl(), false, errs
                    ));
                    continue;
                }

                // Check again to protect against race conditions
                if (productRepository.existsBySku(row.sku().trim().toUpperCase())) {
                    List<String> errs = new ArrayList<>(row.errors());
                    errs.add("SKU already exists in database: " + row.sku());
                    failedRows.add(new ProductImportRowDto(
                            row.rowNumber(), row.name(), row.categoryName(), row.brand(), row.sku(),
                            row.price(), row.originalPrice(), row.stockQuantity(), row.description(),
                            row.specifications(), row.imageUrl(), false, errs
                    ));
                    continue;
                }

                Product product = Product.builder()
                        .name(row.name().trim())
                        .category(category)
                        .brand(row.brand() != null && !row.brand().isBlank() ? row.brand().trim() : null)
                        .sku(row.sku().trim().toUpperCase())
                        .price(row.price())
                        .originalPrice(row.originalPrice())
                        .stockQuantity(row.stockQuantity() != null ? row.stockQuantity() : 0)
                        .description(row.description() != null && !row.description().isBlank() ? row.description().trim() : null)
                        .specifications(row.specifications() != null && !row.specifications().isBlank() ? row.specifications().trim() : null)
                        .rating(BigDecimal.ZERO)
                        .active(true)
                        .build();

                Product saved = productRepository.save(product);
                importedIds.add(saved.getId());

                // If image URL is provided, attach as primary image
                if (row.imageUrl() != null && !row.imageUrl().isBlank()) {
                    ProductImage img = ProductImage.builder()
                            .product(saved)
                            .imageUrl(row.imageUrl().trim())
                            .isPrimary(true)
                            .build();
                    productImageRepository.save(img);
                }
            } catch (Exception ex) {
                log.error("Failed to persist imported row {}: {}", row.rowNumber(), ex.getMessage());
                List<String> errs = new ArrayList<>(row.errors());
                errs.add("Database persistence error: " + ex.getMessage());
                failedRows.add(new ProductImportRowDto(
                        row.rowNumber(), row.name(), row.categoryName(), row.brand(), row.sku(),
                        row.price(), row.originalPrice(), row.stockQuantity(), row.description(),
                        row.specifications(), row.imageUrl(), false, errs
                ));
            }
        }

        String summary = String.format("Bulk import completed: %d products imported successfully, %d skipped or failed.",
                importedIds.size(), failedRows.size());
        log.info(summary);

        return new BulkImportResultResponse(
                parsedRows.size(),
                importedIds.size(),
                failedRows.size(),
                importedIds,
                summary,
                failedRows
        );
    }

    private void validateFileMetadata(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Uploaded file is empty. Please select a valid CSV or XLSX file.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidRequestException("File name cannot be empty.");
        }
        String lower = originalFilename.toLowerCase();
        if (!lower.endsWith(".csv") && !lower.endsWith(".xlsx") && !lower.endsWith(".xls")) {
            throw new InvalidRequestException("Invalid file format (" + originalFilename + "). Only .csv and .xlsx files are supported.");
        }
    }

    private List<ProductImportRowDto> parseAndValidateFile(MultipartFile file) {
        String filename = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        List<RawRow> rawRows;
        try {
            if (filename.endsWith(".csv")) {
                rawRows = parseCsv(file.getInputStream());
            } else {
                rawRows = parseExcel(file.getInputStream());
            }
        } catch (IOException e) {
            log.error("Failed to parse uploaded file", e);
            throw new InvalidRequestException("Could not read uploaded file: " + e.getMessage());
        }

        return validateRawRows(rawRows);
    }

    private List<RawRow> parseCsv(InputStream in) throws IOException {
        List<RawRow> rows = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (CSVParser parser = new CSVParser(new InputStreamReader(in, StandardCharsets.UTF_8), format)) {
            int lineNum = 1;
            for (CSVRecord record : parser) {
                lineNum++;
                rows.add(new RawRow(
                        lineNum,
                        getRecordValue(record, "Name"),
                        getRecordValue(record, "Category"),
                        getRecordValue(record, "Brand"),
                        getRecordValue(record, "SKU"),
                        getRecordValue(record, "Price"),
                        getRecordValue(record, "OriginalPrice"),
                        getRecordValue(record, "Stock"),
                        getRecordValue(record, "Description"),
                        getRecordValue(record, "Specifications"),
                        getRecordValue(record, "ImageUrl")
                ));
            }
        }
        return rows;
    }

    private String getRecordValue(CSVRecord record, String col) {
        try {
            return record.isMapped(col) ? record.get(col) : "";
        } catch (Exception e) {
            return "";
        }
    }

    private List<RawRow> parseExcel(InputStream in) throws IOException {
        List<RawRow> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return rows;

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return rows;

            Map<String, Integer> colMap = new HashMap<>();
            for (Cell cell : headerRow) {
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    colMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
                }
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) continue;

                rows.add(new RawRow(
                        r + 1,
                        getCellString(row, colMap.get("name")),
                        getCellString(row, colMap.get("category")),
                        getCellString(row, colMap.get("brand")),
                        getCellString(row, colMap.get("sku")),
                        getCellString(row, colMap.get("price")),
                        getCellString(row, colMap.get("originalprice")),
                        getCellString(row, colMap.get("stock")),
                        getCellString(row, colMap.get("description")),
                        getCellString(row, colMap.get("specifications")),
                        getCellString(row, colMap.get("imageurl"))
                ));
            }
        }
        return rows;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String getCellString(Row row, Integer colIdx) {
        if (colIdx == null) return "";
        Cell cell = row.getCell(colIdx);
        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    yield String.valueOf((long) num);
                }
                yield String.valueOf(num);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private List<ProductImportRowDto> validateRawRows(List<RawRow> rawRows) {
        Map<String, Category> categoryMap = loadCategoryMap();
        Set<String> seenSkusInFile = new HashSet<>();
        List<ProductImportRowDto> validatedRows = new ArrayList<>();

        for (RawRow r : rawRows) {
            List<String> errors = new ArrayList<>();

            // 1. Validate Name
            String name = r.name() != null ? r.name().trim() : "";
            if (name.isBlank()) {
                errors.add("Product Name is required.");
            } else if (name.length() > 255) {
                errors.add("Product Name exceeds 255 characters limit.");
            }

            // 2. Validate Category
            String categoryName = r.category() != null ? r.category().trim() : "";
            if (categoryName.isBlank()) {
                errors.add("Category is required.");
            } else if (!categoryMap.containsKey(categoryName.toLowerCase())) {
                errors.add("Category '" + categoryName + "' does not exist in catalog.");
            }

            // 3. Validate SKU
            String sku = r.sku() != null ? r.sku().trim().toUpperCase() : "";
            if (sku.isBlank()) {
                errors.add("SKU is required.");
            } else if (sku.length() > 100) {
                errors.add("SKU exceeds 100 characters limit.");
            } else {
                if (!seenSkusInFile.add(sku)) {
                    errors.add("Duplicate SKU within file: " + sku);
                }
                if (productRepository.existsBySku(sku)) {
                    errors.add("SKU already exists in database: " + sku);
                }
            }

            // 4. Validate Price
            BigDecimal price = null;
            if (r.price() == null || r.price().isBlank()) {
                errors.add("Price is required.");
            } else {
                try {
                    price = new BigDecimal(r.price().trim());
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Price must be non-negative.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid monetary price format: '" + r.price() + "'");
                }
            }

            // 5. Validate Original Price (MRP)
            BigDecimal originalPrice = null;
            if (r.originalPrice() != null && !r.originalPrice().isBlank()) {
                try {
                    originalPrice = new BigDecimal(r.originalPrice().trim());
                    if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Original price must be non-negative.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid original price format: '" + r.originalPrice() + "'");
                }
            }

            // 6. Validate Stock
            Integer stock = null;
            if (r.stock() == null || r.stock().isBlank()) {
                errors.add("Stock Quantity is required.");
            } else {
                try {
                    stock = Integer.parseInt(r.stock().trim());
                    if (stock < 0) {
                        errors.add("Stock Quantity must be non-negative.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid stock quantity format: '" + r.stock() + "'");
                }
            }

            // 7. Validate Image URL
            String imageUrl = r.imageUrl() != null ? r.imageUrl().trim() : null;
            if (imageUrl != null && !imageUrl.isBlank()) {
                if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    errors.add("Image URL must start with http:// or https://");
                }
            }

            boolean isValid = errors.isEmpty();
            validatedRows.add(new ProductImportRowDto(
                    r.rowNumber(),
                    name,
                    categoryName,
                    r.brand() != null ? r.brand().trim() : "",
                    sku,
                    price,
                    originalPrice,
                    stock,
                    r.description() != null ? r.description().trim() : "",
                    r.specifications() != null ? r.specifications().trim() : "",
                    imageUrl,
                    isValid,
                    errors
            ));
        }

        return validatedRows;
    }

    private Map<String, Category> loadCategoryMap() {
        Map<String, Category> map = new HashMap<>();
        categoryRepository.findAll().forEach(cat -> map.put(cat.getName().trim().toLowerCase(), cat));
        return map;
    }

    private record RawRow(
            int rowNumber,
            String name,
            String category,
            String brand,
            String sku,
            String price,
            String originalPrice,
            String stock,
            String description,
            String specifications,
            String imageUrl
    ) {}
}
