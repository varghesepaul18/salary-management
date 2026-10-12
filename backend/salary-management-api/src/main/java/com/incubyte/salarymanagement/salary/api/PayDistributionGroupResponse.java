package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.PayDistributionGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Salary metrics for one country, department, and currency group")
public record PayDistributionGroupResponse(
        @Schema(description = "Two-letter country code", example = "US")
        String countryCode,
        @Schema(description = "Employee department", example = "Engineering")
        String department,
        @Schema(description = "Three-letter ISO 4217 currency code; amounts are not converted", example = "USD")
        String currencyCode,
        @Schema(description = "Number of active employees in the group", example = "125")
        Long employeeCount,
        @Schema(description = "Sum of annual base salaries in this group's currency", example = "12500000.00")
        BigDecimal totalAnnualBaseSalary,
        @Schema(description = "Mean annual base salary in this group's currency", example = "100000.00")
        BigDecimal averageAnnualBaseSalary,
        @Schema(description = "Median annual base salary in this group's currency", example = "98000.00")
        BigDecimal medianAnnualBaseSalary,
        @Schema(description = "Minimum annual base salary in this group's currency", example = "60000.00")
        BigDecimal minimumAnnualBaseSalary,
        @Schema(description = "Maximum annual base salary in this group's currency", example = "180000.00")
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
