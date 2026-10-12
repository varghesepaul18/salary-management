package com.incubyte.salarymanagement.salary.domain;

import com.incubyte.salarymanagement.employee.domain.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "salary_history")
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "annual_base_salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal annualBaseSalary;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;

    protected SalaryHistory() {
        // Required by JPA.
    }

    public SalaryHistory(
            Employee employee,
            BigDecimal annualBaseSalary,
            String currencyCode,
            LocalDate effectiveFrom,
            String changedBy,
            Instant changedAt) {
        this.employee = employee;
        this.annualBaseSalary = annualBaseSalary;
        this.currencyCode = currencyCode;
        this.effectiveFrom = effectiveFrom;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getAnnualBaseSalary() {
        return annualBaseSalary;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}