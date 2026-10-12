package com.incubyte.salarymanagement.employee.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A page of employee directory results")
public record EmployeePageResponse(
        @Schema(description = "Employees on this page")
        List<EmployeeSummaryResponse> employees,
        @Schema(description = "Zero-based current page index", example = "0")
        int page,
        @Schema(description = "Requested page size", example = "20")
        int size,
        @Schema(description = "Total employees matching the filters", example = "10000")
        long totalElements,
        @Schema(description = "Total number of result pages", example = "500")
        int totalPages) {
}
