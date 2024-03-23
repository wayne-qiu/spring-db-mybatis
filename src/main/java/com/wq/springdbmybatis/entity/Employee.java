package com.wq.springdbmybatis.entity;

import com.wq.springdbmybatis.mybatis.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class Employee extends Person {

    private long id;
    private String firstName;
    private String lastName;
    private String emailId;
    @Version
    private int version;

}