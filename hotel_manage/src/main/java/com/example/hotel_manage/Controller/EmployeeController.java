package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Pojo.Employee;
import com.example.hotel_manage.Pojo.Enum.EmployeeType;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Service.EmployeeService;
import com.example.hotel_manage.anno.authority_anno.AdminAnno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("employee")
public class EmployeeController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    EmployeeService employeeService;
    @PostMapping("/save")
    @AdminAnno
    Result insertEmployee(@RequestBody Employee employee) {
        employeeService.insertEmployee(employee);
        return Result.success();
    }
    @AdminAnno
    @DeleteMapping("/{id}")
    Result deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }
    @GetMapping("/{id}")
    Result findEmployeeById(@PathVariable int id) {
        Employee employeeById = employeeService.getEmployeeById(id);
        return Result.success(employeeById);
    }


    @PostMapping("/update")
    Result updateEmployee(@RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
        return Result.success();
    }
    @GetMapping("/list")
    Result getEmployee(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size,
                       String name,Boolean gender, EmployeeType type
                       ) {
        PageBean pageBean= employeeService.getEmployee(page,size,name,gender,type);
        return Result.success(pageBean);

    }

}
