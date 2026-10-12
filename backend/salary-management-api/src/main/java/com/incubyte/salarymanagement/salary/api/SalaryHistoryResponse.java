package com.incubyte.salarymanagement.salary.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record SalaryHistoryResponse(
        Long employeeId,
        BigDecimal annualBaseSalary,
        String currencyCode,
        LocalDate effectiveFrom,
        String changedBy,
        Instant changedAt) {
}