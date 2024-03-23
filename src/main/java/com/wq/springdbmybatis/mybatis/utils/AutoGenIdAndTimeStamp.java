package com.wq.springdbmybatis.mybatis.utils;

import com.wq.springdbmybatis.entity.BaseEntity;
import com.wq.springdbmybatis.mybatis.GlobalConstant;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Mybatis插入时自动填充Id，包括主键id、创建人id（createBy）、修改人id（updateBy）
 * 注意：需要实体类有id、createBy、updateBy属性
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
})
public class AutoGenIdAndTimeStamp implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(AutoGenIdAndTimeStamp.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 对应上面类注解的args，获取需要的MappedStatement对象
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];

        // 插入对象，即实体类如：SysUser
        Object obj = args[1];

        /**
         * 插入或更新的时候，赋予createBy或updateBy值
         */
        // 从MDC(Mapped Diagnostic Context)中获取用户id
        // https://medium.com/javarevisited/mapped-diagnostic-context-mdc-6447b598736d

        String userIdStr = MDC.get(GlobalConstant.MDC_USER_ID);
        Long userId = null;
        if (!StringUtils.isEmpty(userIdStr)) {
            userId = Long.parseLong(userIdStr);
        }

        // 识别SQL类型，看是INSERT还是UPDATE
        if (SqlCommandType.INSERT.equals(statement.getSqlCommandType())) {
            // 单个插入，BaseEntity是我的实体类的父类，该类有id、createBy、updateBy等通用属性
            if (obj instanceof BaseEntity) {
                BaseEntity entity = ((BaseEntity) obj);
                assignIdIfNull(entity, userId);
                assignDefaultValue((BaseEntity) entity);
            }
            // 批量插入
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                List<?> list = (List<?>) map.get("list");
                if (list != null) {
                    for (Object entity : list) {
                        assignIdIfNull((BaseEntity) entity, userId);
                        // 批量操作不同于单个操作，单个操作支持部分字段，批量操作不支持，所以要提前设定默认值
                        assignDefaultValue((BaseEntity) entity);
                    }
                }
            }
        }

        if (SqlCommandType.UPDATE.equals(statement.getSqlCommandType())) {
            // 单个更新
            if (obj instanceof BaseEntity) {
                BaseEntity entity = ((BaseEntity) obj);
                entity.setUpdateBy(Optional.ofNullable(userId).orElse(0L));
                entity.setUpdateTime(new Date());
            }
            // 批量更新
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                List<?> list = (List<?>) map.get("list");
                if (list != null) {
                    for (Object item : list) {
                        BaseEntity entity = ((BaseEntity) item);
                        entity.setUpdateBy(Optional.ofNullable(userId).orElse(0L));
                        entity.setUpdateTime(new Date());
                    }
                }
            }
        }

        return invocation.proceed();
    }

    /**
     * 赋予id、createBy值
     * @param entity 实体类
     * @param userId 操作用户id
     */
    private void assignIdIfNull(BaseEntity entity, Long userId) {
        // 如果数据没有id值则自动分配
//        if (entity.getId() == null || entity.getId() == 0L) {
//        // entity.setId(IdGenerator.INSTANCE.nextId());
//            entity.setId(UUID.randomUUID().getLeastSignificantBits());
//
//        }
        // 如果数据没有创建者并且当前登录用户id不为空
        if (entity.getCreateBy() == null && userId != null) {
            entity.setCreateBy(userId);
        }
    }

    /**
     * 批量插入的时候给基础属性赋予默认值，不然会失败
     * 这里是插入的最后一级，所以优先级最高，业务代码内手动设置的也会被这里覆盖掉，也应该被覆盖
     * @param entity
     */
    private void assignDefaultValue(BaseEntity entity) {
        entity.setUpdateBy(0L);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}