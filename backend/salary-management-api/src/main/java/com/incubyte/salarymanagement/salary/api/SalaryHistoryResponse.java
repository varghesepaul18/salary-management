package com.incubyte.salarymanagement.salary.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Recorded salary change")
public record SalaryHistoryResponse(
        @Schema(description = "Employee database identifier", example = "42")
        Long employeeId,
        @Schema(description = "Annual base salary amount", example = "95000.00")
        BigDecimal annualBaseSalary,
        @Schema(description = "Three-letter ISO 4217 currency code", example = "USD")
        String currencyCode,
        @Schema(description = "Date from which this salary is effective", example = "2026-10-13")
        LocalDate effectiveFrom,
        @Schema(description = "Authenticated HR manager who recorded the change")
        String changedBy,
        @Schema(description = "Timestamp when the change was recorded", example = "2026-10-13T10:30:00Z")
        Instant changedAt) {
}
