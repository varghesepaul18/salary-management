package com.incubyte.salarymanagement.employee.application;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import com.incubyte.salarymanagement.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmployeeDirectoryServiceTest {

    private EmployeeRepository employeeRepository;
    private EmployeeDirectoryService service;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        service = new EmployeeDirectoryService(employeeRepository);
    }

    @Test
    void normalizesFiltersAndRequestsStablePageFromRepository() {
        Page<Employee> expected = new PageImpl<>(List.of(mock(Employee.class)));

        when(employeeRepository.search(
                eq("amara"),
                eq("IN"),
                eq("Engineering"),
                eq(EmploymentStatus.ACTIVE),
                any(Pageable.class)))
                .thenReturn(expected);

        Page<Employee> result = service.search(
                " amara ",
                "in",
                " Engineering ",
                EmploymentStatus.ACTIVE,
                2,
                25);

        assertSame(expected, result);

        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository).search(
                eq("amara"),
                eq("IN"),
                eq("Engineering"),
                eq(EmploymentStatus.ACTIVE),
                pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(25, pageable.getPageSize());
        assertEquals(
                List.of(
                        Sort.Order.asc("lastName"),
                        Sort.Order.asc("firstName"),
                        Sort.Order.asc("id")),
                pageable.getSort().toList());
    }

    @Test
    void rejectsPageSizeAboveLimitWithoutQueryingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.search(null, null, null, null, 0, 101));

        verifyNoInteractions(employeeRepository);
    }

    @Test
    void rejectsInvalidCountryCodeWithoutQueryingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.search(null, "India", null, null, 0, 20));

        verifyNoInteractions(employeeRepository);
    }
}