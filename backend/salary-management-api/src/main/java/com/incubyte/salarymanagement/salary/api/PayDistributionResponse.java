package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.PayDistributionReport;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Salary pay distribution report for the requested date")
public record PayDistributionResponse(
        @Schema(description = "Date used to select each employee's latest effective salary", example = "2026-10-13")
        LocalDate asOfDate,
        @Schema(description = "Aggregated groups by country, department, and currency")
        List<PayDistributionGroupResponse> groups) {

    public static PayDistributionResponse from(PayDistributionReport report) {
        return new PayDistributionResponse(
                report.asOfDate(),
                report.groups().stream()
                        .map(PayDistributionGroupResponse::from)
                        .toList());
    }
}
