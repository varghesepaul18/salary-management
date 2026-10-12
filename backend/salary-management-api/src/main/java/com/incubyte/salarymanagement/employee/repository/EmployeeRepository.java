package com.incubyte.salarymanagement.employee.repository;

import com.incubyte.salarymanagement.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}