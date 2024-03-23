package com.wq.springdbmybatis.converter;

import com.wq.springdbmybatis.dto.PersonDto;
import com.wq.springdbmybatis.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonConverter {
    @Mapping(target = "sex", source = "sex")
    @Mapping(target = "age", source = "age")
    PersonDto personToPersonDto(Person person);
}
