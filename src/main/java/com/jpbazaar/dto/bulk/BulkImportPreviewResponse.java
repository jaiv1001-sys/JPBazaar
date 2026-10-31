package com.jpbazaar.dto.bulk;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Bulk import validation preview summary and row diagnostics")
public record BulkImportPreviewResponse(
        @Schema(description = "Total data rows parsed from file", example = "50")
        int totalRows,

        @Schema(description = "Count of valid rows ready for import", example = "48")
        int validRows,

        @Schema(description = "Count of invalid rows with errors", example = "2")
        int invalidRows,

        @Schema(description = "Count of duplicate SKUs detected", example = "1")
        int duplicateSkusCount,

        @Schema(description = "Whether the file contains any validation errors", example = "false")
        boolean hasErrors,

        @Schema(description = "List of all parsed rows with their individual validation status")
        List<ProductImportRowDto> rows
) {}
