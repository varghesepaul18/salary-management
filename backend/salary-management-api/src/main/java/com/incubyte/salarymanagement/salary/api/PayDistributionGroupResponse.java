package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.PayDistributionGroup;

import java.math.BigDecimal;

public record PayDistributionGroupResponse(
        String countryCode,
        String department,
        String currencyCode,
        Long employeeCount,
        BigDecimal totalAnnualBaseSalary,
        BigDecimal averageAnnualBaseSalary,
        BigDecimal medianAnnualBaseSalary,
        BigDecimal minimumAnnualBaseSalary,
        BigDecimal maximumAnnualBaseSalary) {

    public static PayDistributionGroupResponse from(PayDistributionGroup group) {
        return new PayDistributionGroupResponse(
                group.countryCode(),
                group.department(),
                group.currencyCode(),
                group.employeeCount(),
                group.totalAnnualBaseSalary(),
                group.averageAnnualBaseSalary(),
                group.medianAnnualBaseSalary(),
                group.minimumAnnualBaseSalary(),
                group.maximumAnnualBaseSalary());
    }
}