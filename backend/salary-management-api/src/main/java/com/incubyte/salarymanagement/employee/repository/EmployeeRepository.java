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
            where (coalesce(:searchTerm, '') = ''
                or lower(employee.employeeNumber) like lower(concat('%', coalesce(:searchTerm, ''), '%'))
                or lower(employee.firstName) like lower(concat('%', coalesce(:searchTerm, ''), '%'))
                or lower(employee.lastName) like lower(concat('%', coalesce(:searchTerm, ''), '%')))
              and (coalesce(:countryCode, '') = '' or employee.countryCode = :countryCode)
              and (coalesce(:department, '') = ''
                   or lower(employee.department) = lower(coalesce(:department, '')))
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
