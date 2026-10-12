package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.PayDistributionGroup;
import com.incubyte.salarymanagement.salary.application.PayDistributionReport;
import com.incubyte.salarymanagement.salary.application.SalaryAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalaryAnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalaryAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaryAnalyticsService salaryAnalyticsService;

    @Test
    void returnsPayDistributionForRequestedDateAndFilters() throws Exception {
        LocalDate asOfDate = LocalDate.of(2026, 10, 1);
        var group = new PayDistributionGroup(
                "IN",
                "Engineering",
                "INR",
                120L,
                new BigDecimal("120000000"),
                new BigDecimal("1000000"),
                new BigDecimal("950000"),
                new BigDecimal("600000"),
                new BigDecimal("1800000"));

        when(salaryAnalyticsService.getPayDistribution(
                asOfDate, "IN", "Engineering"))
                .thenReturn(new PayDistributionReport(asOfDate, List.of(group)));

        mockMvc.perform(get("/api/analytics/pay-distribution")
                        .param("asOfDate", "2026-10-01")
                        .param("country", "IN")
                        .param("department", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asOfDate").value("2026-10-01"))
                .andExpect(jsonPath("$.groups[0].countryCode").value("IN"))
                .andExpect(jsonPath("$.groups[0].currencyCode").value("INR"))
                .andExpect(jsonPath("$.groups[0].employeeCount").value(120))
                .andExpect(jsonPath("$.groups[0].medianAnnualBaseSalary").value(950000));

        verify(salaryAnalyticsService).getPayDistribution(
                asOfDate, "IN", "Engineering");
    }

    @Test
    void returnsBadRequestForInvalidCountryFilter() throws Exception {
        when(salaryAnalyticsService.getPayDistribution(null, "India", null))
                .thenThrow(new IllegalArgumentException(
                        "country must be a two-letter country code"));

        mockMvc.perform(get("/api/analytics/pay-distribution")
                        .param("country", "India"))
                .andExpect(status().isBadRequest());
    }
}