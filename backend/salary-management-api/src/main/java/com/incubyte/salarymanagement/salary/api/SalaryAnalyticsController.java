package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.SalaryAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@Slf4j
@Tag(name = "Salary analytics", description = "Aggregated salary distribution insights")
@SecurityRequirement(name = "bearerAuth")
public class SalaryAnalyticsController {

    private final SalaryAnalyticsService salaryAnalyticsService;

    public SalaryAnalyticsController(SalaryAnalyticsService salaryAnalyticsService) {
        this.salaryAnalyticsService = salaryAnalyticsService;
    }

    @GetMapping("/pay-distribution")
    @Operation(summary = "Get salary pay distribution", description = "Summarizes active employees' current annual base salaries by country, department, and currency as of the requested date. Different currencies are reported separately without conversion.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pay distribution report returned"),
            @ApiResponse(responseCode = "400", description = "Invalid date or filter value"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token")
    })
    public PayDistributionResponse getPayDistribution(
            @Parameter(description = "Include the latest salary effective on or before this date; defaults to today", example = "2026-10-13")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @Parameter(description = "Optional two-letter country code, for example US")
            @RequestParam(required = false) String country,
            @Parameter(description = "Optional department filter, case-insensitive")
            @RequestParam(required = false) String department) {
        log.debug("Received pay distribution request: asOfDate={}, country={}, department={}",
                asOfDate, country, department);
        return PayDistributionResponse.from(
                salaryAnalyticsService.getPayDistribution(
                        asOfDate, country, department));
    }
}
