package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Pojo.Employee;
import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Service.EmployeeService;
import com.example.hotel_manage.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/login")
    Result login(@RequestParam String username, @RequestParam String password) {
        Employee login = employeeService.login(username,password);
        Map<String, Object> claims = new HashMap<>();
        if (login != null) {
            claims.put("id",login.getEmployeeId());
            claims.put("type",login.getType());
            claims.put("name",login.getName());
            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }
        return Result.error("登录失败");
    }


}
