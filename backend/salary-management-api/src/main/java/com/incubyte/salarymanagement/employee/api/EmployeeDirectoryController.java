package com.incubyte.salarymanagement.employee.api;

import com.incubyte.salarymanagement.employee.application.EmployeeDirectoryService;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
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
public class EmployeeDirectoryController {

    private final EmployeeDirectoryService employeeDirectoryService;

    public EmployeeDirectoryController(EmployeeDirectoryService employeeDirectoryService) {
        this.employeeDirectoryService = employeeDirectoryService;
    }

    @GetMapping
    public EmployeePageResponse search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(defaultValue = "0") int page,
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