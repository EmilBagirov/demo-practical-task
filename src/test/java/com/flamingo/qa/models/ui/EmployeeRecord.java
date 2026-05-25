package com.flamingo.qa.models.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRecord {
    private String firstName;
    private String lastName;
    private String age;
    private String email;
    private String salary;
    private String department;
}
