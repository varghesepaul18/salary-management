package com.incubyte.salarymanagement.salary.application;

import com.incubyte.salarymanagement.salary.repository.PayDistributionProjection;
import com.incubyte.salarymanagement.salary.repository.SalaryHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SalaryAnalyticsServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-13T00:00:00Z");

    private SalaryHistoryRepository salaryHistoryRepository;
    private SalaryAnalyticsService service;

    @BeforeEach
    void setUp() {
        salaryHistoryRepository = mock(SalaryHistoryRepository.class);
        service = new SalaryAnalyticsService(
                salaryHistoryRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void defaultsDateNormalizesFiltersAndMapsDistribution() {
        PayDistributionProjection projection = mock(PayDistributionProjection.class);
        when(projection.getCountryCode()).thenReturn("IN");
        when(projection.getDepartment()).thenReturn("Engineering");
        when(projection.getCurrencyCode()).thenReturn("INR");
        when(projection.getEmployeeCount()).thenReturn(120L);
        when(projection.getTotalAnnualBaseSalary()).thenReturn(new BigDecimal("120000000"));
        when(projection.getAverageAnnualBaseSalary()).thenReturn(new BigDecimal("1000000"));
        when(projection.getMedianAnnualBaseSalary()).thenReturn(new BigDecimal("950000"));
        when(projection.getMinimumAnnualBaseSalary()).thenReturn(new BigDecimal("600000"));
        when(projection.getMaximumAnnualBaseSalary()).thenReturn(new BigDecimal("1800000"));

        when(salaryHistoryRepository.findPayDistribution(
                LocalDate.of(2026, 10, 13), "IN", "Engineering"))
                .thenReturn(List.of(projection));

        PayDistributionReport report = service.getPayDistribution(
                null, " in ", " Engineering ");

        assertEquals(LocalDate.of(2026, 10, 13), report.asOfDate());
        assertEquals(1, report.groups().size());
        assertEquals("INR", report.groups().getFirst().currencyCode());
        assertEquals(120L, report.groups().getFirst().employeeCount());
        assertEquals(
                new BigDecimal("950000"),
                report.groups().getFirst().medianAnnualBaseSalary());

        verify(salaryHistoryRepository).findPayDistribution(
                LocalDate.of(2026, 10, 13), "IN", "Engineering");
    }

    @Test
    void rejectsInvalidCountryWithoutQueryingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getPayDistribution(null, "India", null));

        verifyNoInteractions(salaryHistoryRepository);
    }
}