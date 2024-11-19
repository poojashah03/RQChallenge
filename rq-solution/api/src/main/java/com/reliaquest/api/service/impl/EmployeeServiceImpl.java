package com.reliaquest.api.service.impl;

import com.reliaquest.api.exception.CustomNoDataFoundException;
import com.reliaquest.api.exception.EmpInternalServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.response.EmployeeListResponse;
import com.reliaquest.api.response.EmployeeResponse;
import com.reliaquest.api.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests;
import reactor.util.retry.RetryBackoffSpec;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.reliaquest.api.constants.EmployeeConstants.TOO_MANY_REQUEST_EXCEPTION_MSG;
import static com.reliaquest.api.utils.HelperUtils.*;

@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private WebClient webClient;

    RetryBackoffSpec retrySpecs = getRetrySpecs();

    @Value("${employee.server.base-url}")
    private String BASE_URL;

    public EmployeeServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Request to fetch all employees from {} ", BASE_URL);
        try {
            String strResponse = getEmployeeData(webClient, BASE_URL);
            EmployeeListResponse employeeResponse = processResponse(strResponse, EmployeeListResponse.class);
            log.info(employeeResponse.toString());
            log.info("Fetched {} employees successfully.", employeeResponse.getData().size());
            return employeeResponse.getData();
        } catch (TooManyRequests e) {
            log.warn(TOO_MANY_REQUEST_EXCEPTION_MSG);
            throw e;
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw handleCustomException(e, "Failed to retrieve all employees data.");
        }
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.info("Fetching employee details with id: {}", id);
        try {
            String strResponse = getEmployeeData(webClient, BASE_URL + "/" + id);
            EmployeeResponse employeeResponse = processResponse(strResponse, EmployeeResponse.class);
            log.info(employeeResponse.getData().toString());
            log.info("Successfully fetched employee details with id: {}", id);
            if (employeeResponse.getData() == null) {
                throw new CustomNoDataFoundException("Employee with given id:" + id + " not found.");
            }
            return employeeResponse.getData();
        } catch (TooManyRequests e) {
            log.warn(TOO_MANY_REQUEST_EXCEPTION_MSG);
            throw e;
        } catch (CustomNoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            throw handleCustomException(e, "Failed to retrieve employee by id.");
        }
    }


    @Override
    public Employee createEmployee(EmployeeRequest employeeInput) {
        log.info("Creating new employee...");
        try {
            EmployeeResponse employeeResponse = postEmployeeData(webClient, BASE_URL, employeeInput);
            log.info(employeeResponse.getData().toString());
            log.info("Successfully created new employee.");
            return employeeResponse.getData();
        } catch (TooManyRequests e) {
            log.warn(TOO_MANY_REQUEST_EXCEPTION_MSG);
            throw e;
        } catch (Exception e) {
            throw handleCustomException(e, "Failed to create a new employee.");
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee by id: {}", id);
        try {
            Employee employee = getEmployeeById(id);
            deleteEmployeeData(webClient, employee.getEmployee_name());
            log.info("Successfully deleted employee with id: {}", id);
            return employee.getEmployee_name();
        } catch (TooManyRequests e) {
            log.warn(TOO_MANY_REQUEST_EXCEPTION_MSG);
            throw e;
        } catch (Exception e) {
            throw handleCustomException(e, "Failed to delete employee by id.");
        }
    }

    @Override
    public List<Employee> getTop10HighestEarningEmployeeNames() throws Exception {
        log.info("Fetching top 10 highest-earning employees");
        try {
            return getAllEmployees().stream()
                    .sorted(Comparator.comparingInt(Employee::getEmployee_salary).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw handleCustomException(e, "retrieve top 10 highest-earning employees");
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchStr) {
        log.info("Searching for employees with name containing '{}'", searchStr);
        try {
            List<Employee> matchingEmployees = getAllEmployees().stream()
                    .filter(employee -> employee.getEmployee_name().contains(searchStr))
                    .collect(Collectors.toList());
            if (matchingEmployees.isEmpty()) {
                log.warn("No employees found matching '{}' in the name", searchStr);
                throw new CustomNoDataFoundException("No employees found with '" + searchStr + "' containing in the names.");
            }
            log.info("Found {} employees with '{}' containing in the names.", matchingEmployees.size(), searchStr);
            return matchingEmployees;
        } catch (CustomNoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            throw handleCustomException(e, "Failed to search employees by given '" + searchStr + "' in the names");
        }
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        log.info("Finding the highest salary among employees...");
        try {
            return getAllEmployees().stream()
                    .map(Employee::getEmployee_salary)
                    .max(Comparator.naturalOrder())
                    .orElseThrow(() -> new EmpInternalServiceException("No employees found to with the highest salary."));
        } catch (Exception e) {
            throw handleCustomException(e, "Failed to find the  highest salary of the employees.");
        }
    }


}
