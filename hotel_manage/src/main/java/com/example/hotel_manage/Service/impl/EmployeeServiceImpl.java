package com.example.hotel_manage.Service.impl;

import com.example.hotel_manage.Mapper.EmployeeMapper;
import com.example.hotel_manage.Pojo.Employee;
import com.example.hotel_manage.Pojo.Enum.EmployeeType;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Service.EmployeeService;
import com.example.hotel_manage.Utils.HttpUtils;
import com.example.hotel_manage.anno.authority_anno.AdminAnno;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    private HttpUtils httpUtils;
    @Override
    public Employee login(String username, String password) {
        return employeeMapper.login(username, password);
    }

    @Override
    public void insertEmployee(Employee employee) {
        employeeMapper.insert(employee);
    }
    //此处插入权限校验逻辑 在操作employee的权限type时，只有管理员被允许操作
    @Override
    public void updateEmployee(Employee employee) {
        Employee byUsername = employeeMapper.getByUsername(employee.getUsername());
        if(!(byUsername.getType().equals(employee.getType()))){
            updateEmployeeByAdmin(employee);
            return;
        }
        employeeMapper.update(employee);
    }

    @AdminAnno
    public void updateEmployeeByAdmin(Employee employee){
        employeeMapper.update(employee);
    }

    @Override
    public void deleteEmployee(int id) {
        employeeMapper.deleteById(id);
    }

    @Override
    public PageBean getEmployee(Integer page, Integer pageSize, String name, Boolean gender, EmployeeType type) {
        PageHelper.startPage(page, pageSize);
        List<Employee> list=employeeMapper.list(name,gender,type);
        PageInfo<Employee> pageInfo=new PageInfo<>(list);
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getList());
        return pageBean;
    }

    @Override
    public Employee getEmployeeById(int id) {

        return employeeMapper.getById(id);
    }
}
