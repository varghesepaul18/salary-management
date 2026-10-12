package com.incubyte.salarymanagement.salary.repository;

import java.math.BigDecimal;

public interface PayDistributionProjection {

    String getCountryCode();

    String getDepartment();

    String getCurrencyCode();

    Long getEmployeeCount();

    BigDecimal getTotalAnnualBaseSalary();

    BigDecimal getAverageAnnualBaseSalary();

    BigDecimal getMedianAnnualBaseSalary();

    BigDecimal getMinimumAnnualBaseSalary();

    BigDecimal getMaximumAnnualBaseSalary();
}