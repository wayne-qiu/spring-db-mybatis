package com.wq.springdbmybatis.mapper;

import com.wq.springdbmybatis.entity.Employee;
import com.wq.springdbmybatis.mybatis.orm.GenericCrudMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper extends GenericCrudMapper<Employee, Long> {

    @Select("SELECT * FROM employees WHERE id = #{id}")
    //resultMap
    @Results({
            @Result(property = "emailId", column = "email_address")
    })
    Employee fetchById(long id);

    @Select("select * from employees")
    @Results({
            @Result(property = "emailId", column = "email_address")
    })
    List<Employee> fetchAll();

    @Insert("INSERT INTO employees(id, first_name, last_name, email_address, createTime, createBy, updateBy) " +
            " VALUES (#{id}, #{firstName}, #{lastName}, #{emailId}, #{createTime}, #{createBy}, #{updateBy})")
    int insert(Employee employee);

    @Update("Update employees set first_name=#{firstName}, updateBy=#{updateBy}," +
            " last_name=#{lastName}, email_address=#{emailId}, updateTime=#{updateTime} where id=#{id}")
    int update(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}")
    int deleteById(long id);

    @Delete("DELETE FROM employees WHERE id = #{employee.id}")
    int delete(Employee employee);
}