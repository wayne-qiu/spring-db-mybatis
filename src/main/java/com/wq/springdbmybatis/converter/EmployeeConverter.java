package com.wq.springdbmybatis.converter;

import com.wq.springdbmybatis.dto.EmployeeDto;
import com.wq.springdbmybatis.entity.Employee;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeConverter /*extends PersonMapper*/ {

    /**
     * @InheritConfiguration
     * https://mapstruct.org/documentation/dev/api/org/mapstruct/InheritConfiguration.html
     *   Inherit the configuration from another mapping method (declared on the same mapper type)
     *   or prototype method (declared on a mapper config class referenced via Mapper.config())
     *   This is useful when you want to use the same mapping configuration to the annotated method as well
     * https://mapstruct.org/documentation/stable/reference/html/#mapping-configuration-inheritance
     *
     * as doc, InheritConfiguration refers to the same mapper type, no matter the class extends.
     */
    @InheritConfiguration
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "emailId", source = "emailId")
    EmployeeDto employeeToEmployeeDto(Employee employee);
}
