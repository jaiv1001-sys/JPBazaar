package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.bulk.BulkImportPreviewResponse;
import com.jpbazaar.dto.bulk.BulkImportResultResponse;
import com.jpbazaar.service.BulkImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products/import")
@RequiredArgsConstructor
@Tag(name = "Product Bulk Import", description = "High-throughput CSV and Excel bulk product catalog import engine with preview, validation, and error reporting")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class BulkImportController {

    private final BulkImportService bulkImportService;

    @GetMapping(value = "/template/csv", produces = "text/csv")
    @Operation(summary = "Download sample CSV import template (Admin only)", description = "Generates a RFC 4180 standard CSV template with required headers and curated product examples.")
    public ResponseEntity<byte[]> downloadCsvTemplate() {
        byte[] csvData = bulkImportService.generateCsvTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"jpbazaar_products_template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    @GetMapping(value = "/template/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Download sample Excel XLSX import template (Admin only)", description = "Generates a styled Excel (.xlsx) workbook with header styling, column widths, and sample rows.")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] excelData = bulkImportService.generateExcelTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"jpbazaar_products_template.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Preview and validate bulk import file (Admin only)", description = "Parses uploaded CSV or Excel file, executes row-level validations, and reports duplicate SKUs or schema violations without persisting data.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File validated successfully with diagnostics"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file format or empty payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)")
    })
    public ResponseEntity<ApiResponse<BulkImportPreviewResponse>> previewImport(
            @Parameter(description = "CSV or XLSX file to preview") @RequestParam("file") MultipartFile file
    ) {
        BulkImportPreviewResponse response = bulkImportService.previewImport(file);
        return ResponseEntity.ok(ApiResponse.success("File parsed and validated successfully", response));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Execute confirmed bulk product import (Admin only)", description = "Persists all valid product records within a transactional boundary, attaches product images, and evicts catalog Redis caches.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Bulk import executed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or all rows failed validation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)")
    })
    public ResponseEntity<ApiResponse<BulkImportResultResponse>> executeImport(
            @Parameter(description = "CSV or XLSX file to import") @RequestParam("file") MultipartFile file
    ) {
        BulkImportResultResponse response = bulkImportService.executeImport(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response.summaryMessage(), response));
    }
}
