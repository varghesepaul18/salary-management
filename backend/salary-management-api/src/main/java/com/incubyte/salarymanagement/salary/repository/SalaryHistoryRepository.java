package com.incubyte.salarymanagement.salary.repository;

import com.incubyte.salarymanagement.salary.domain.SalaryHistory;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    Optional<SalaryHistory>
    findFirstByEmployee_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Long employeeId,
            LocalDate asOf);
}