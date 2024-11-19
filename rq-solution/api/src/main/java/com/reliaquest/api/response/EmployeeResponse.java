package com.reliaquest.api.response;

import com.reliaquest.api.model.Employee;
import lombok.*;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    public Employee data;
    public String status;
}
