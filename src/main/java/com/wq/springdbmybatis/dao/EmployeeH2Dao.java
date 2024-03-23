package com.wq.springdbmybatis.dao;

import com.wq.springdbmybatis.entity.Employee;
import com.wq.springdbmybatis.mapper.EmployeeMapper;
import com.wq.springdbmybatis.mybatis.orm.GenericCrudRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeH2Dao extends GenericCrudRepository<Employee, Long> {

    public EmployeeH2Dao(EmployeeMapper mapper) {
        super(mapper);
    }
}