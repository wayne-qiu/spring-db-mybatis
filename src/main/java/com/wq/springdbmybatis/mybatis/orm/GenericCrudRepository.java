package com.wq.springdbmybatis.mybatis.orm;

import java.util.List;

public abstract class GenericCrudRepository<T, PK> {

    protected GenericCrudMapper mapper;

    public GenericCrudRepository(GenericCrudMapper mapper) {
        this.mapper = mapper;
    }

    public T fetchById(PK id) {
        return (T)mapper.fetchById(id);
    }


    public List<T> fetch(SearchCriteria searchCriteria) {
        return mapper.fetch(searchCriteria);
    }

    public List<T> fetchAll() {
        return mapper.fetchAll();
    }

    public int insert(T o) {
        return mapper.insert(o);
    }

    public int update(T o) {
        return mapper.update(o);
    }

    public int deleteById(PK id) {
        return mapper.deleteById(id);
    }
    public int delete(T o) {
        return mapper.delete(o);
    }
}