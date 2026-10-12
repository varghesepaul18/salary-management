package com.incubyte.salarymanagement.employee.api;

import java.util.List;

public record EmployeePageResponse(
        List<EmployeeSummaryResponse> employees,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}