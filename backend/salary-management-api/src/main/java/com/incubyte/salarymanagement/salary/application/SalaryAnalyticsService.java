package com.incubyte.salarymanagement.salary.application;

import com.incubyte.salarymanagement.salary.repository.PayDistributionProjection;
import com.incubyte.salarymanagement.salary.repository.SalaryHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@Transactional(readOnly = true)
public class SalaryAnalyticsService {

    private final SalaryHistoryRepository salaryHistoryRepository;
    private final Clock clock;

    public SalaryAnalyticsService(
            SalaryHistoryRepository salaryHistoryRepository,
            Clock clock) {
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.clock = clock;
    }

    public PayDistributionReport getPayDistribution(
            LocalDate requestedAsOfDate,
            String requestedCountryCode,
            String requestedDepartment) {

        LocalDate asOfDate = requestedAsOfDate == null
                ? LocalDate.now(clock)
                : requestedAsOfDate;
        String countryCode = normalizeCountryCode(requestedCountryCode);
        String department = normalizeDepartment(requestedDepartment);

        List<PayDistributionGroup> groups = salaryHistoryRepository
                .findPayDistribution(asOfDate, countryCode, department)
                .stream()
                .map(this::toGroup)
                .toList();

        log.debug("Pay distribution generated: asOfDate={}, groupCount={}",
                asOfDate, groups.size());

        return new PayDistributionReport(asOfDate, groups);
    }

    private PayDistributionGroup toGroup(PayDistributionProjection projection) {
        return new PayDistributionGroup(
                projection.getCountryCode(),
                projection.getDepartment(),
                projection.getCurrencyCode(),
                projection.getEmployeeCount(),
                projection.getTotalAnnualBaseSalary(),
                projection.getAverageAnnualBaseSalary(),
                projection.getMedianAnnualBaseSalary(),
                projection.getMinimumAnnualBaseSalary(),
                projection.getMaximumAnnualBaseSalary());
    }

    private String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return null;
        }

        String normalized = countryCode.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException(
                    "country must be a two-letter country code");
        }
        return normalized;
    }

    private String normalizeDepartment(String department) {
        if (department == null || department.isBlank()) {
            return null;
        }

        String normalized = department.trim();
        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                    "department must be at most 100 characters");
        }
        return normalized;
    }
}