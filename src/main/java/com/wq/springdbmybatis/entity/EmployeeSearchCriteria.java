package com.wq.springdbmybatis.entity;

import com.wq.springdbmybatis.mybatis.orm.SearchCriteria;
import lombok.Data;

@Data
public class EmployeeSearchCriteria implements SearchCriteria {
    private String firstName;
}