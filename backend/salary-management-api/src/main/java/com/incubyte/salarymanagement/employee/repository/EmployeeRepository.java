package com.incubyte.salarymanagement.employee.repository;

import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            select employee
            from Employee employee
            where (:searchTerm is null
                or lower(employee.employeeNumber) like lower(concat('%', :searchTerm, '%'))
                or lower(employee.firstName) like lower(concat('%', :searchTerm, '%'))
                or lower(employee.lastName) like lower(concat('%', :searchTerm, '%')))
              and (:countryCode is null or employee.countryCode = :countryCode)
              and (:department is null or lower(employee.department) = lower(:department))
              and (:employmentStatus is null
                   or employee.employmentStatus = :employmentStatus)
            """)
    Page<Employee> search(
            @Param("searchTerm") String searchTerm,
            @Param("countryCode") String countryCode,
            @Param("department") String department,
            @Param("employmentStatus") EmploymentStatus employmentStatus,
            Pageable pageable);
}