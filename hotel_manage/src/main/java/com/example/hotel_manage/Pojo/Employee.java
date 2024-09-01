package com.example.hotel_manage.Pojo;

import com.example.hotel_manage.Pojo.Enum.EmployeeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Integer employeeId;
    private String password;
    private String name;
    private String username;
    private EmployeeType type;
    private String employmentStatus;
    private String phoneNumber;
    //1表示男，0表示女
    private Boolean gender;
}
