package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.SalaryHistoryService;
import com.incubyte.salarymanagement.salary.domain.SalaryHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalaryHistoryController.class)
class SalaryHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaryHistoryService salaryHistoryService;

    @Test
    void recordsSalaryChangeUsingAuthenticatedUsername() throws Exception {
        LocalDate effectiveFrom = LocalDate.of(2026, 10, 1);
        Instant changedAt = Instant.parse("2026-10-12T11:00:00Z");
        SalaryHistory saved = mock(SalaryHistory.class);

        when(saved.getAnnualBaseSalary()).thenReturn(new BigDecimal("95000"));
        when(saved.getCurrencyCode()).thenReturn("USD");
        when(saved.getEffectiveFrom()).thenReturn(effectiveFrom);
        when(saved.getChangedBy()).thenReturn("hr.manager");
        when(saved.getChangedAt()).thenReturn(changedAt);
        when(salaryHistoryService.recordSalaryChange(
                17L,
                new BigDecimal("95000"),
                "USD",
                effectiveFrom,
                "hr.manager"))
                .thenReturn(saved);

        mockMvc.perform(post("/api/employees/17/salary-history")
                        .with(user("hr.manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "annualBaseSalary": 95000,
                                  "currencyCode": "USD",
                                  "effectiveFrom": "2026-10-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(17))
                .andExpect(jsonPath("$.annualBaseSalary").value(95000))
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.changedBy").value("hr.manager"));

        verify(salaryHistoryService).recordSalaryChange(
                17L,
                new BigDecimal("95000"),
                "USD",
                effectiveFrom,
                "hr.manager");
    }

    @Test
    void rejectsNonPositiveSalaryBeforeCallingService() throws Exception {
        mockMvc.perform(post("/api/employees/17/salary-history")
                        .with(user("hr.manager"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "annualBaseSalary": 0,
                                  "currencyCode": "USD",
                                  "effectiveFrom": "2026-10-01"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(salaryHistoryService);
    }
}