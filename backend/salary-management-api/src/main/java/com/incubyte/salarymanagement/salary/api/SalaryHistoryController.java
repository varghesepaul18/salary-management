package com.incubyte.salarymanagement.salary.api;

import com.incubyte.salarymanagement.salary.application.SalaryHistoryService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/employees/{employeeId}/salary-history")
@Tag(name = "Salary history", description = "Review and record employee salary changes")
@SecurityRequirement(name = "bearerAuth")
public class SalaryHistoryController {

    private final SalaryHistoryService salaryHistoryService;

    public SalaryHistoryController(SalaryHistoryService salaryHistoryService) {
        this.salaryHistoryService = salaryHistoryService;
    }

    @GetMapping
    @Operation(summary = "Get employee salary history", description = "Returns all recorded salary entries for an employee, ordered by effective date from newest to oldest.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary history returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public List<SalaryHistoryResponse> getSalaryHistory(
            @Parameter(description = "Employee database identifier", example = "42")
            @PathVariable Long employeeId) {
        log.info("Received salary history request for employeeId={}", employeeId);
        return salaryHistoryService.getSalaryHistory(employeeId).stream()
                .map(entry -> new SalaryHistoryResponse(
                        employeeId,
                        entry.getAnnualBaseSalary(),
                        entry.getCurrencyCode(),
                        entry.getEffectiveFrom(),
                        entry.getChangedBy(),
                        entry.getChangedAt()))
                .toList();
    }

    @PostMapping
    @Operation(summary = "Record a salary change", description = "Adds an employee salary record with an effective date. The authenticated HR manager is recorded as the change author.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Salary change recorded",
                    content = @Content(schema = @Schema(implementation = SalaryHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid employee ID or salary request"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token"),
            @ApiResponse(responseCode = "500", description = "Salary change could not be recorded")
    })
    public ResponseEntity<SalaryHistoryResponse> recordSalaryChange(
            @Parameter(description = "Employee database identifier", example = "42")
            @PathVariable Long employeeId,
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New salary values and their effective date",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecordSalaryChangeRequest.class)))
            RecordSalaryChangeRequest request,
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
