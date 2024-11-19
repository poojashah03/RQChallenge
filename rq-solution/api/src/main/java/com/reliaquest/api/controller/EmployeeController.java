package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/employee")
public class EmployeeController {

    @Autowired
    EmployeeServiceImpl employeeService;

    /**
     * API to get all Employees List
     *
     * @return - Employee List, with status message
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllEmployees() {
        List<Employee> employeeList = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    /**
     * API to get employee details by given id
     *
     * @param id
     * @return - Return the employee details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmployeeById(@PathVariable("id") String id) {
        Employee employeeResponse = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeResponse, HttpStatus.OK);
    }

    /**
     * API to create employee
     *
     * @param employee
     * @return - Returns the same employee details in case of success
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createEmployee(@RequestBody EmployeeRequest employee) {
        Employee employeeResponse = employeeService.createEmployee(employee);
        return new ResponseEntity<Object>(employeeResponse, HttpStatus.OK);
    }

    /**
     * API to search the employees which has the given nameStr in thier names
     *
     * @param searchStr
     * @return - Returns the list of employees
     */
    @GetMapping("/name/{searchStr}")
    public ResponseEntity<Object> getEmployeesByNameSearch(@PathVariable("searchStr") String searchStr) {
        List<Employee> listEmployees = employeeService.getEmployeesByNameSearch(searchStr);
        return new ResponseEntity<>(listEmployees, HttpStatus.OK);
    }

    /**
     * API to get the highest salary of the employee
     *
     * @return - int salary value
     */
    @GetMapping("/highest/salary")
    public ResponseEntity<Object> getHighestSalaryOfEmployees() {
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    /**
     * API to get the top 10 highest paid employee
     *
     * @return - Returns the employee list, else returns the empty list
     */
    @GetMapping("/highest/top10")
    public ResponseEntity<Object> getTop10HighestEarningEmployeeNames() throws Exception {
        List<Employee> listEmployees = employeeService.getTop10HighestEarningEmployeeNames();
        return new ResponseEntity<>(listEmployees, HttpStatus.OK);
    }

    /**
     * API to delete employee by id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/v1/employee/delete/{id}")
    public String deleteEmployeeById(@PathVariable("id") String id) {
        return employeeService.deleteEmployeeById(id);
    }
}
