package com.incubyte.salarymanagement.salary.application;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.repository.EmployeeRepository;
import com.incubyte.salarymanagement.salary.domain.SalaryHistory;
import com.incubyte.salarymanagement.salary.repository.SalaryHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SalaryHistoryServiceTest {

    private static final Long EMPLOYEE_ID = 17L;
    private static final LocalDate EFFECTIVE_FROM = LocalDate.of(2026, 10, 1);
    private static final Instant RECORDED_AT =
            Instant.parse("2026-10-12T11:00:00Z");

    private EmployeeRepository employeeRepository;
    private SalaryHistoryRepository salaryHistoryRepository;
    private SalaryHistoryService service;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        salaryHistoryRepository = mock(SalaryHistoryRepository.class);
        service = new SalaryHistoryService(
                employeeRepository,
                salaryHistoryRepository,
                Clock.fixed(RECORDED_AT, ZoneOffset.UTC));
    }

    @Test
    void recordsSalaryChangeForExistingEmployee() {
        Employee employee = mock(Employee.class);
        BigDecimal salary = new BigDecimal("95000.0000");

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));
        when(salaryHistoryRepository.existsByEmployee_IdAndEffectiveFrom(
                EMPLOYEE_ID, EFFECTIVE_FROM))
                .thenReturn(false);
        when(salaryHistoryRepository.save(any(SalaryHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SalaryHistory result = service.recordSalaryChange(
                EMPLOYEE_ID, salary, "USD", EFFECTIVE_FROM, "hr.manager");

        assertSame(employee, result.getEmployee());
        assertEquals(salary, result.getAnnualBaseSalary());
        assertEquals("USD", result.getCurrencyCode());
        assertEquals(EFFECTIVE_FROM, result.getEffectiveFrom());
        assertEquals("hr.manager", result.getChangedBy());
        assertEquals(RECORDED_AT, result.getChangedAt());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(salaryHistoryRepository)
                .existsByEmployee_IdAndEffectiveFrom(EMPLOYEE_ID, EFFECTIVE_FROM);
        verify(salaryHistoryRepository).save(result);
    }

    @Test
    void rejectsUnknownEmployee() {
        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> service.recordSalaryChange(
                        EMPLOYEE_ID,
                        new BigDecimal("95000"),
                        "USD",
                        EFFECTIVE_FROM,
                        "hr.manager"));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(salaryHistoryRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateEffectiveDate() {
        Employee employee = mock(Employee.class);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));
        when(salaryHistoryRepository.existsByEmployee_IdAndEffectiveFrom(
                EMPLOYEE_ID, EFFECTIVE_FROM))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.recordSalaryChange(
                        EMPLOYEE_ID,
                        new BigDecimal("95000"),
                        "USD",
                        EFFECTIVE_FROM,
                        "hr.manager"));

        verify(salaryHistoryRepository, never()).save(any());
    }

    @Test
    void rejectsNonPositiveSalaryBeforeAccessingRepositories() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.recordSalaryChange(
                        EMPLOYEE_ID,
                        BigDecimal.ZERO,
                        "USD",
                        EFFECTIVE_FROM,
                        "hr.manager"));

        verifyNoInteractions(employeeRepository, salaryHistoryRepository);
    }
}