package com.incubyte.salarymanagement.employee.application;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import com.incubyte.salarymanagement.employee.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EmployeeDirectoryService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_SEARCH_LENGTH = 100;

    private final EmployeeRepository employeeRepository;

    public EmployeeDirectoryService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<Employee> search(
            String searchTerm,
            String countryCode,
            String department,
            EmploymentStatus employmentStatus,
            int pageNumber,
            int pageSize) {

        if (pageNumber < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        String normalizedSearchTerm = normalizeText(searchTerm, "search");
        String normalizedDepartment = normalizeText(department, "department");
        String normalizedCountryCode = normalizeCountryCode(countryCode);

        var pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(
                        Sort.Order.asc("lastName"),
                        Sort.Order.asc("firstName"),
                        Sort.Order.asc("id")));

        Page<Employee> results = employeeRepository.search(
                normalizedSearchTerm,
                normalizedCountryCode,
                normalizedDepartment,
                employmentStatus,
                pageable);

        log.debug("Employee search completed: page={}, size={}, returned={}, total={}",
                pageNumber, pageSize, results.getNumberOfElements(), results.getTotalElements());

        return results;
    }

    private String normalizeText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_SEARCH_LENGTH) {
            throw new IllegalArgumentException(fieldName + " must be at most 100 characters");
        }
        return normalized;
    }

    private String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return null;
        }

        String normalized = countryCode.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("country must be a two-letter country code");
        }
        return normalized;
    }
}