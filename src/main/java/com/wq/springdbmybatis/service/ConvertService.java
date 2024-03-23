package com.wq.springdbmybatis.service;

import com.wq.springdbmybatis.dto.EmployeeDto;
import com.wq.springdbmybatis.dto.PersonDto;
import com.wq.springdbmybatis.entity.Employee;
import com.wq.springdbmybatis.entity.Person;
import com.wq.springdbmybatis.converter.EmployeeConverter;
import com.wq.springdbmybatis.converter.PersonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ConvertService {

    @Autowired
    @Qualifier("personConverterImpl")
    private PersonConverter personConverter;

    @Autowired
    private EmployeeConverter employeeConverter;

    public void transfer(){
        Person person = new Person("M", 30);
        PersonDto personDto = personConverter.personToPersonDto(person);
        System.out.println(personDto.toString());

        Employee employee = Employee.builder()
                .sex("M")
                .age(30)
                .id(10011L)
                .firstName("Ramesh")
                .lastName("Fadatare")
                .emailId("ramesh@gmail.com")
                .build();
        EmployeeDto employeeDto = employeeConverter.employeeToEmployeeDto(employee);
        System.out.println(employeeDto.toString());
    }
}
