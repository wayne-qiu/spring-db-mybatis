package com.wq.springdbmybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class EmployeeDto extends PersonDto {

    private long id;
    private String firstName;
    private String lastName;
    private String emailId;

}
