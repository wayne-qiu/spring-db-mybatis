package com.wq.springdbmybatis.service;

import com.wq.springdbmybatis.dao.EmployeeH2Dao;
import com.wq.springdbmybatis.entity.Employee;
import com.wq.springdbmybatis.mybatis.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class H2Service {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmployeeH2Dao employeeH2Dao;

    public void insert() {
        MDC.put(GlobalConstant.MDC_USER_ID, "007");

        logger.info("Inserting -> {}", employeeH2Dao.insert(new Employee(10011L, "Ramesh", "Fadatare", "ramesh@gmail.com", 0)));
        logger.info("Inserting -> {}", employeeH2Dao.insert(new Employee(10012L, "John", "Cena", "john@gmail.com", 0)));
        logger.info("Inserting -> {}", employeeH2Dao.insert(new Employee(10013L, "tony", "stark", "stark@gmail.com", 0)));

        logger.info("Employee id 10011 -> {}", employeeH2Dao.fetchById(10011L));
        // update
        logger.info("Update 10003 -> {}", employeeH2Dao.update(new Employee(10013L, "ram", "Stark", "ramesh123@gmail.com", 0)));

        logger.info("Update 10003 -> {}", employeeH2Dao.update(new Employee(10013L, "ram", "Stark", "ramesh123@gmail.com", 1)));

        employeeH2Dao.deleteById(10011L);

        logger.info("All users -> {}", employeeH2Dao.fetchAll());
    }

}
