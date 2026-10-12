package com.incubyte.salarymanagement.employee.api;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Employee directory summary; salary details are not included")
public record EmployeeSummaryResponse(
        @Schema(description = "Employee database identifier", example = "42")
        Long id,
        @Schema(description = "Organization-assigned employee number", example = "ACME-000042")
        String employeeNumber,
        @Schema(description = "Employee first name")
        String firstName,
        @Schema(description = "Employee last name")
        String lastName,
        @Schema(description = "Two-letter country code", example = "US")
        String countryCode,
        @Schema(description = "Employee department", example = "Engineering")
        String department,
        @Schema(description = "Current employment status")
        EmploymentStatus employmentStatus) {

    public static EmployeeSummaryResponse from(Employee employee) {
        return new EmployeeSummaryResponse(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getCountryCode(),
                employee.getDepartment(),
                employee.getEmploymentStatus());
    }
}
