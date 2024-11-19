package com.reliaquest.api.response;

import com.reliaquest.api.model.Employee;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListResponse {
    public List<Employee> data;
    public String status;
}
