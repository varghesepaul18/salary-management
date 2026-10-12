package com.incubyte.salarymanagement.employee.api;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;

public record EmployeeSummaryResponse(
        Long id,
        String employeeNumber,
        String firstName,
        String lastName,
        String countryCode,
        String department,
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