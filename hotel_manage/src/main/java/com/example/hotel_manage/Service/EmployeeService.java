package com.example.hotel_manage.Service;

import com.example.hotel_manage.Pojo.Employee;
import com.example.hotel_manage.Pojo.Enum.EmployeeType;
import com.example.hotel_manage.Pojo.PageBean;

public interface EmployeeService {
    
    Employee login(String username, String password);

    void insertEmployee(Employee employee);

    void updateEmployee(Employee employee);

    void deleteEmployee(int id);

    PageBean getEmployee(Integer page, Integer pageSize, String name, Boolean gender, EmployeeType type);

    Employee getEmployeeById(int id);
}
