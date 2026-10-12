package com.incubyte.salarymanagement.salary.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordSalaryChangeRequest(
        @NotNull
        @DecimalMin(value = "0.0001")
        BigDecimal annualBaseSalary,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}")
        String currencyCode,

        @NotNull
        LocalDate effectiveFrom) {
}