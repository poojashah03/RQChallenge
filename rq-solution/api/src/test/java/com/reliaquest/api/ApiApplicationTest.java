package com.reliaquest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest
public class ApiApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    EmployeeServiceImpl iEmployeeService;

    @MockBean
    WebClient webClient;

    @Autowired
    private MockMvc mockMvc;

    @Value("${employee.server.base.url}")
    private String BASE_URL;
    Employee employee = new Employee("d8d67f81-dc0c-40ea-99a2-05640c4cef2a", "Pooja",
            4000, 23, "Sr.MTS", "test@test.com");
    String name = "Pooja";
    EmployeeRequest createEmployee = new EmployeeRequest("Pooja", 4000, 25, "Sr.MTS");

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void testgetAllEmployees_OK() throws Exception {
        when(iEmployeeService.getAllEmployees())
                .thenReturn(List.of(employee));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/all"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employee_name").value(name));
    }

    @Test
    void testgetEmployeeById_OK() throws Exception {
        String uuid = "42d1bee9-3e39-4479-acdd-020224052403";
        when(iEmployeeService.getEmployeeById(uuid))
                .thenReturn(employee);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/" + uuid))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employee_name").value(name));
    }

    @Test
    void testgetEmployeeHighestSalary_OK() throws Exception {
        when(iEmployeeService.getHighestSalaryOfEmployees())
                .thenReturn(40000);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/highest/salary"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testgetTop10HighSalaryEmployees_OK() throws Exception {
        when(iEmployeeService.getTop10HighestEarningEmployeeNames())
                .thenReturn(List.of(employee));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/highest/top10"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testgetEmployeeByNameSearch_OK() throws Exception {
        when(iEmployeeService.getEmployeesByNameSearch(name))
                .thenReturn(List.of(employee));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/name/" + name))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employee_name").value(name));
    }

    @Test
    void testcreateEmployee_OK() throws Exception {
        when(iEmployeeService.createEmployee(createEmployee)).thenReturn(
                employee);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createEmployee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employee_name").value(name));
    }

    @Test
    void testgetAllEmployees_Exception() throws Exception {
        when(iEmployeeService.getAllEmployees())
                .thenThrow(new Exception("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/all"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetAllEmployees_IOException() throws Exception {
        when(iEmployeeService.getAllEmployees())
                .thenThrow(new Exception("Server Error Occurred"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/all"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetEmployeeById_RunTimeException() throws Exception {
        when(iEmployeeService.getEmployeeById(null))
                .thenThrow(new RuntimeException("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/null"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetEmployeeById_IOException() throws Exception {
        when(iEmployeeService.getEmployeeById(null))
                .thenThrow(new Exception("Server Error Occurred"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/null"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetEmployeeHighestSalary_RunTimeException() throws Exception {
        when(iEmployeeService.getHighestSalaryOfEmployees())
                .thenThrow(new RuntimeException("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/highest/salary"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetEmployeeHighestSalary_IOException() throws Exception {
        when(iEmployeeService.getHighestSalaryOfEmployees())
                .thenThrow(new Exception("Server Error Occurred"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/highest/salary"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetTop10HighSalaryEmployees_RunTimeException() throws Exception {
        when(iEmployeeService.getTop10HighestEarningEmployeeNames())
                .thenThrow(new RuntimeException("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/highest"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }


    @Test
    void testgetEmployeeByNameSearch_RunTimeException() throws Exception {
        when(iEmployeeService.getEmployeesByNameSearch(null))
                .thenThrow(new RuntimeException("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/name"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testgetEmployeeByNameSearch_IOException() throws Exception {
        when(iEmployeeService.getEmployeesByNameSearch(null))
                .thenThrow(new Exception("Server Error Occurred"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/employee/name"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testcreateEmployee_RunTimeException() throws Exception {
        when(iEmployeeService.createEmployee(createEmployee))
                .thenThrow(new RuntimeException("Unable to access resource"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testcreateEmployee_IOException() throws Exception {
        when(iEmployeeService.createEmployee(createEmployee))
                .thenThrow(new Exception("Server Error Occurred"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
