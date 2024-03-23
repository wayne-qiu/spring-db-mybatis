package com.wq.springdbmybatis.mybatis.utils;

import com.wq.springdbmybatis.mybatis.annotation.CreatedBy;
import com.wq.springdbmybatis.mybatis.annotation.CreatedDate;
import com.wq.springdbmybatis.mybatis.annotation.LastModifiedBy;
import com.wq.springdbmybatis.mybatis.annotation.LastModifiedDate;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AuditingInterceptor implements Interceptor {
    private final String param1 = "param1";	//参数为map时，有param1这个键，具体原因不知道。

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        //找到数据表对应的PO
        Object parameter = invocation.getArgs()[1];
        Object po = null;
        if (parameter instanceof Map) {
            Map map = (Map) parameter;
            if (map.containsKey(param1)) {
                po = map.get(param1);
            }
        } else {
            po = parameter;
        }

        if (po == null) {
            return invocation.proceed();
        }

        //当前时间和操作人
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUser = "admin";

        //遍历PO的字段，查找并修改其中被审计字段的值
        Field[] fields = po.getClass().getDeclaredFields();
        if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (AnnotationUtils.getAnnotation(field, CreatedBy.class) != null && SqlCommandType.INSERT == sqlCommandType && currentUser != null) {
                    field.set(po, currentUser);
                }
                if (AnnotationUtils.getAnnotation(field, CreatedDate.class) != null && SqlCommandType.INSERT == sqlCommandType) {
                    field.set(po, currentDate);
                }
                if (AnnotationUtils.getAnnotation(field, LastModifiedBy.class) != null && currentUser != null) {
                    field.set(po, currentUser);
                }
                if (AnnotationUtils.getAnnotation(field, LastModifiedDate.class) != null) {
                    field.set(po, currentDate);
                }
                field.setAccessible(accessible);
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

}

