package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.SalaryHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/employees/{employeeId}/salary-history")
public class SalaryHistoryController {

    private final SalaryHistoryService salaryHistoryService;

    public SalaryHistoryController(SalaryHistoryService salaryHistoryService) {
        this.salaryHistoryService = salaryHistoryService;
    }

    @PostMapping
    public ResponseEntity<SalaryHistoryResponse> recordSalaryChange(
            @PathVariable Long employeeId,
            @Valid @RequestBody RecordSalaryChangeRequest request,
            Principal principal) {

        var saved = salaryHistoryService.recordSalaryChange(
                employeeId,
                request.annualBaseSalary(),
                request.currencyCode(),
                request.effectiveFrom(),
                principal.getName());

        var response = new SalaryHistoryResponse(
                employeeId,
                saved.getAnnualBaseSalary(),
                saved.getCurrencyCode(),
                saved.getEffectiveFrom(),
                saved.getChangedBy(),
                saved.getChangedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}