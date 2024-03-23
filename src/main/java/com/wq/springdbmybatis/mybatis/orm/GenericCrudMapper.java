package com.wq.springdbmybatis.mybatis.orm;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenericCrudMapper<T, PK> {
    Object fetchById(PK id);
    List<T> fetch(@Param("searchCriteria") SearchCriteria searchCriteria);
    List<T> fetchAll();
    int insert(T o);
    int update(T o);
    int deleteById(PK id);
    int delete(T o);
}