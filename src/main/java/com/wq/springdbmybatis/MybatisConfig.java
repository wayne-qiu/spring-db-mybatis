package com.wq.springdbmybatis;

import com.wq.springdbmybatis.mybatis.utils.AutoGenIdAndTimeStamp;
import com.wq.springdbmybatis.mybatis.utils.OptimisticLockInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

//    @Bean
//    public LockInterceptor LockInterceptor() {
//        return new LockInterceptor();
//    }

    @Bean
    public OptimisticLockInterceptor OptimisticLockInterceptor() {
        return new OptimisticLockInterceptor();
    }

    @Bean
    public AutoGenIdAndTimeStamp AutoGenIdAndTimeStamp() {
        return new AutoGenIdAndTimeStamp();
    }

}