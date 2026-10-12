package com.incubyte.salarymanagement.salary.application;

import java.math.BigDecimal;

public record PayDistributionGroup(
        String countryCode,
        String department,
        String currencyCode,
        Long employeeCount,
        BigDecimal totalAnnualBaseSalary,
        BigDecimal averageAnnualBaseSalary,
        BigDecimal medianAnnualBaseSalary,
        BigDecimal minimumAnnualBaseSalary,
        BigDecimal maximumAnnualBaseSalary) {
}