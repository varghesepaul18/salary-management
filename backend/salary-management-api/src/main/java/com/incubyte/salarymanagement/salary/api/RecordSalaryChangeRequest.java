package com.incubyte.salarymanagement.salary.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Salary change details")
public record RecordSalaryChangeRequest(
        @Schema(description = "Annual base salary amount in the specified currency", example = "95000.00", minimum = "0.0001")
        @NotNull
        @DecimalMin(value = "0.0001")
        BigDecimal annualBaseSalary,

        @Schema(description = "Three-letter ISO 4217 currency code", example = "USD", pattern = "[A-Z]{3}")
        @NotBlank
        @Pattern(regexp = "[A-Z]{3}")
        String currencyCode,

        @Schema(description = "Date from which this salary becomes effective", example = "2026-10-13")
        @NotNull
        LocalDate effectiveFrom) {
}
