package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;

import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees() throws Exception;

    Employee getEmployeeById(String id) throws Exception;

    List<Employee> getEmployeesByNameSearch(String name) throws Exception;

    List<Employee> getTop10HighestEarningEmployeeNames() throws Exception;

    Employee createEmployee(EmployeeRequest employee) throws Exception;

    int getHighestSalaryOfEmployees() throws Exception;

    String deleteEmployeeById(String id) throws Exception;
}
