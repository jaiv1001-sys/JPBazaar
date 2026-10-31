package com.jpbazaar.dto.bulk;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Bulk import execution result and database persistence summary")
public record BulkImportResultResponse(
        @Schema(description = "Total data rows processed", example = "50")
        int totalProcessed,

        @Schema(description = "Count of successfully persisted products", example = "48")
        int successfullyImported,

        @Schema(description = "Count of skipped or failed rows", example = "2")
        int failedRows,

        @Schema(description = "List of newly generated product IDs")
        List<Long> importedProductIds,

        @Schema(description = "Executive import result summary message", example = "Bulk import completed: 48 products imported, 2 skipped.")
        String summaryMessage,

        @Schema(description = "Detailed list of rows that failed during execution")
        List<ProductImportRowDto> failureDetails
) {}
