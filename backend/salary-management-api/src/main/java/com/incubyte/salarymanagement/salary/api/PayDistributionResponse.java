package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.PayDistributionReport;

import java.time.LocalDate;
import java.util.List;

public record PayDistributionResponse(
        LocalDate asOfDate,
        List<PayDistributionGroupResponse> groups) {

    public static PayDistributionResponse from(PayDistributionReport report) {
        return new PayDistributionResponse(
                report.asOfDate(),
                report.groups().stream()
                        .map(PayDistributionGroupResponse::from)
                        .toList());
    }
}