package com.incubyte.salarymanagement.salary.repository;

import com.incubyte.salarymanagement.salary.domain.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    Optional<SalaryHistory>
    findFirstByEmployee_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Long employeeId,
            LocalDate asOf);

    boolean existsByEmployee_IdAndEffectiveFrom(Long employeeId, LocalDate effectiveFrom);

    @Query(value = """
            WITH current_salary AS (
                SELECT DISTINCT ON (salary.employee_id)
                    salary.employee_id,
                    salary.annual_base_salary,
                    salary.currency_code
                FROM salary_history salary
                WHERE salary.effective_from <= :asOf
                ORDER BY salary.employee_id, salary.effective_from DESC
            )
            SELECT
                employee.country_code AS "countryCode",
                employee.department AS "department",
                current_salary.currency_code AS "currencyCode",
                COUNT(*) AS "employeeCount",
                SUM(current_salary.annual_base_salary) AS "totalAnnualBaseSalary",
                AVG(current_salary.annual_base_salary) AS "averageAnnualBaseSalary",
                PERCENTILE_CONT(0.5) WITHIN GROUP (
                    ORDER BY current_salary.annual_base_salary
                )::numeric(19, 4) AS "medianAnnualBaseSalary",
                MIN(current_salary.annual_base_salary) AS "minimumAnnualBaseSalary",
                MAX(current_salary.annual_base_salary) AS "maximumAnnualBaseSalary"
            FROM employees employee
            JOIN current_salary
                ON current_salary.employee_id = employee.id
            WHERE employee.employment_status = 'ACTIVE'
              AND (CAST(:countryCode AS varchar) IS NULL
                   OR employee.country_code = :countryCode)
              AND (CAST(:department AS varchar) IS NULL
                   OR LOWER(employee.department) = LOWER(:department))
            GROUP BY
                employee.country_code,
                employee.department,
                current_salary.currency_code
            ORDER BY
                employee.country_code,
                employee.department,
                current_salary.currency_code
            """, nativeQuery = true)
    List<PayDistributionProjection> findPayDistribution(
            @Param("asOf") LocalDate asOf,
            @Param("countryCode") String countryCode,
            @Param("department") String department);
}