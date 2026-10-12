package com.incubyte.salarymanagement.employee.api;

import com.incubyte.salarymanagement.employee.application.EmployeeDirectoryService;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/employees")
@Slf4j
@Tag(name = "Employees", description = "Search and browse the employee directory")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeDirectoryController {

    private final EmployeeDirectoryService employeeDirectoryService;

    public EmployeeDirectoryController(EmployeeDirectoryService employeeDirectoryService) {
        this.employeeDirectoryService = employeeDirectoryService;
    }

    @GetMapping
    @Operation(summary = "Search employees", description = "Returns a page of employees with optional name, employee number, country, department, and employment-status filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination values"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token")
    })
    public EmployeePageResponse search(
            @Parameter(description = "Matches employee name or employee number")
            @RequestParam(required = false) String search,
            @Parameter(description = "Two-letter country code, for example US")
            @RequestParam(required = false) String country,
            @Parameter(description = "Exact department name, ignoring case")
            @RequestParam(required = false) String department,
            @Parameter(description = "Employment status")
            @RequestParam(required = false) EmploymentStatus status,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of employees per page (1-100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<EmployeeSummaryResponse> results = employeeDirectoryService
                    .search(search, country, department, status, page, size)
                    .map(EmployeeSummaryResponse::from);

            return new EmployeePageResponse(
                    results.getContent(),
                    results.getNumber(),
                    results.getSize(),
                    results.getTotalElements(),
                    results.getTotalPages());
        } catch (IllegalArgumentException exception) {
            log.warn("Rejected employee directory request: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
