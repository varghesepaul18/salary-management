package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.SalaryAnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@Slf4j
public class SalaryAnalyticsController {

    private final SalaryAnalyticsService salaryAnalyticsService;

    public SalaryAnalyticsController(SalaryAnalyticsService salaryAnalyticsService) {
        this.salaryAnalyticsService = salaryAnalyticsService;
    }

    @GetMapping("/pay-distribution")
    public PayDistributionResponse getPayDistribution(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department) {

        try {
            log.debug("Received pay distribution request: asOfDate={}, country={}, department={}",
                    asOfDate, country, department);
            return PayDistributionResponse.from(
                    salaryAnalyticsService.getPayDistribution(
                            asOfDate, country, department));
        } catch (IllegalArgumentException exception) {
            log.warn("Rejected pay distribution request: {}", exception.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}