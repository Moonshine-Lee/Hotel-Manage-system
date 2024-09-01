package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Employee;
import com.example.hotel_manage.Pojo.Enum.EmployeeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Insert("INSERT INTO employee (password, name, username, type, phone_number) " +
            "VALUES (#{password}, #{name}, #{username}, #{type}, #{phoneNumber})")
    int insert(Employee employee);

    @Select("SELECT * FROM employee WHERE employee_id = #{id}")
    Employee getById(@Param("id") Integer id);

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee getByUsername(@Param("username") String username);

    void update( Employee employee);


    @Delete("DELETE FROM employee WHERE employee_id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("select * from employee where username=#{username} and password=#{password}")
    Employee login(@Param("username") String username,@Param("password") String password);

    List<Employee> list(@Param("name") String name,@Param(("gender")) Boolean gender,@Param("type") EmployeeType type);
}
