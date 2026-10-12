package com.incubyte.salarymanagement.salary.application;

import java.time.LocalDate;
import java.util.List;

public record PayDistributionReport(
        LocalDate asOfDate,
        List<PayDistributionGroup> groups) {

    public PayDistributionReport {
        groups = List.copyOf(groups);
    }
}