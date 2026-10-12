package com.incubyte.salarymanagement.employee.api;

import com.incubyte.salarymanagement.employee.application.EmployeeDirectoryService;
import com.incubyte.salarymanagement.employee.domain.Employee;
import com.incubyte.salarymanagement.employee.domain.EmploymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeDirectoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeDirectoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeDirectoryService employeeDirectoryService;

    @Test
    void returnsPagedEmployeeDirectory() throws Exception {
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(12L);
        when(employee.getEmployeeNumber()).thenReturn("E-0012");
        when(employee.getFirstName()).thenReturn("Sam");
        when(employee.getLastName()).thenReturn("Jose");
        when(employee.getCountryCode()).thenReturn("US");
        when(employee.getDepartment()).thenReturn("Finance");
        when(employee.getEmploymentStatus()).thenReturn(EmploymentStatus.ACTIVE);

        var pageable = PageRequest.of(1, 20);
        var page = new PageImpl<>(List.of(employee), pageable, 42);

        when(employeeDirectoryService.search(
                "sam", "US", "Finance", EmploymentStatus.ACTIVE, 1, 20))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("search", "sam")
                        .param("country", "US")
                        .param("department", "Finance")
                        .param("status", "ACTIVE")
                        .param("page", "1")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[0].id").value(12))
                .andExpect(jsonPath("$.employees[0].employeeNumber").value("E-0012"))
                .andExpect(jsonPath("$.employees[0].firstName").value("Sam"))
                .andExpect(jsonPath("$.employees[0].employmentStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(42))
                .andExpect(jsonPath("$.totalPages").value(3));

        verify(employeeDirectoryService).search(
                "sam", "US", "Finance", EmploymentStatus.ACTIVE, 1, 20);
    }

    @Test
    void returnsBadRequestWhenPageSizeIsInvalid() throws Exception {
        when(employeeDirectoryService.search(null, null, null, null, 0, 101))
                .thenThrow(new IllegalArgumentException("size must be between 1 and 100"));

        mockMvc.perform(get("/api/employees").param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}