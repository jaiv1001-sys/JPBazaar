package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Daily sales trend aggregate for charting revenue and order volume")
public record DailySalesResponse(
        @Schema(description = "Timeline date", example = "2026-10-28")
        LocalDate date,

        @Schema(description = "Total orders placed on this date", example = "35")
        long orderCount,

        @Schema(description = "Total revenue earned on this date", example = "18450.75")
        BigDecimal revenue
) {
}
