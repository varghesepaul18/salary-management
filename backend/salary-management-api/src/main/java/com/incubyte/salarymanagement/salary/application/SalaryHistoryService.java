package com.incubyte.salarymanagement.salary.application;

import com.incubyte.salarymanagement.employee.repository.EmployeeRepository;
import com.incubyte.salarymanagement.salary.domain.SalaryHistory;
import com.incubyte.salarymanagement.salary.repository.SalaryHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Currency;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class SalaryHistoryService {

    private final EmployeeRepository employeeRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final Clock clock;

    public SalaryHistoryService(
            EmployeeRepository employeeRepository,
            SalaryHistoryRepository salaryHistoryRepository,
            Clock clock) {
        this.employeeRepository = employeeRepository;
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.clock = clock;
    }

    public SalaryHistory recordSalaryChange(
            Long employeeId,
            BigDecimal annualBaseSalary,
            String currencyCode,
            LocalDate effectiveFrom,
            String changedBy) {

        log.info("Recording salary change for employeeId={}, annualBaseSalary={}, currencyCode={}, effectiveFrom={}, changedBy={}",
                employeeId, annualBaseSalary, currencyCode, effectiveFrom, changedBy);

        validate(employeeId, annualBaseSalary, currencyCode, effectiveFrom, changedBy);

        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Employee not found: " + employeeId));

        if (salaryHistoryRepository.existsByEmployee_IdAndEffectiveFrom(
                employeeId, effectiveFrom)) {
            log.warn("Salary change already exists for employeeId={} and effectiveFrom={}", employeeId, effectiveFrom);
            throw new IllegalStateException(
                    "A salary change already exists for this employee and effective date");
        }

        var salaryHistory = new SalaryHistory(
                employee,
                annualBaseSalary,
                currencyCode,
                effectiveFrom,
                changedBy,
                clock.instant());

        var savedSalaryHistory = salaryHistoryRepository.save(salaryHistory);
        log.info("Saved salary change for employeeId={} with effectiveFrom={}", employeeId, effectiveFrom);
        return savedSalaryHistory;
    }

    private void validate(
            Long employeeId,
            BigDecimal annualBaseSalary,
            String currencyCode,
            LocalDate effectiveFrom,
            String changedBy) {

        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("employeeId must be positive");
        }
        if (annualBaseSalary == null || annualBaseSalary.signum() <= 0) {
            throw new IllegalArgumentException("annualBaseSalary must be positive");
        }
        if (currencyCode == null || !currencyCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("currencyCode must be a three-letter ISO code");
        }
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("currencyCode must be a valid ISO 4217 code");
        }
        if (effectiveFrom == null) {
            throw new IllegalArgumentException("effectiveFrom is required");
        }
        if (changedBy == null || changedBy.isBlank() || changedBy.length() > 100) {
            throw new IllegalArgumentException("changedBy must contain 1 to 100 characters");
        }
    }
}