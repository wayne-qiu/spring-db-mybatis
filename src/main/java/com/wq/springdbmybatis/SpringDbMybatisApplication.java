package com.wq.springdbmybatis;

import com.wq.springdbmybatis.service.H2Service;
import com.wq.springdbmybatis.service.ConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDbMybatisApplication implements CommandLineRunner {


    @Autowired
    private ConvertService convertService;

    @Autowired
    private H2Service h2Service;

    @Override
    public void run(String...args) throws Exception {
        convertService.transfer();
        System.out.println("-------H2 Service--------");
        h2Service.insert();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDbMybatisApplication.class, args);
    }
}
