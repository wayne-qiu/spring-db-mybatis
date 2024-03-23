package com.wq.springdbmybatis.mybatis.utils;

import com.wq.springdbmybatis.mybatis.annotation.Version;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * 乐观锁插件
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}),
})
public class OptimisticLockInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(OptimisticLockInterceptor.class);

    /**
     * 记录没有悲观锁的表，就不用去判断了
     */
    private List<String> ignoreEntityList;

    public OptimisticLockInterceptor() {
        ignoreEntityList = new ArrayList<>();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        /**
         * 实现思路：
         * 1. 判断语句类型，仅支持update
         * 2. 获取数据对象，提取出version值，设该值为oldVersion，令其+1设该值为newVersion
         * 3. 获取SQL语句，用JSqlParser工具修改version参数值为newVersion，添加version的查询条件：where version = oldVersion
         * 4. 将新的SQL语句覆盖进去，然后继续执行
         */

        // 下面代码直接抄
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 操作类型
        SqlCommandType sqlType = ms.getSqlCommandType();
        if (sqlType != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }

        // 获取数据对象
        final Object parameterObject = metaObject.getValue("delegate.boundSql.parameterObject");

        // 不支持数组
        if (parameterObject instanceof List) {
            return invocation.proceed();
        }

        // 有没有记录过
        String key = parameterObject.getClass().getName();
        if (ignoreEntityList.contains(key)) {
            return invocation.proceed();
        }

        // 检查是否有乐观锁字段
        final Field[] fieldList = parameterObject.getClass().getDeclaredFields();
        // 乐观锁注解校验
        Version version = null;
        Field versionField = null;
        for (Field field : fieldList) {
            Version annotation = field.getDeclaredAnnotation(Version.class);
            if (annotation != null) {
                if (version != null) {
                    throw new Exception("同一个实体类不能有两个乐观锁字段：" + key);
                }
                version = annotation;
                versionField = field;
            }
        }

        if (version == null) {
            // 记录在案，下次就省的处理了
            ignoreEntityList.add(key);
            return invocation.proceed();
        }

        String versionFieldName = versionField.getName();

        Object value = metaObject.getValue("delegate.boundSql.parameterObject." + versionFieldName);
        if (value == null) {
            throw new Exception("乐观锁字段的值不能为空");
        }

        // 旧值
        int oldVersion = Integer.parseInt((value.toString()));
        if (oldVersion < 0) {
            return invocation.proceed();
        }
        // 新值
        int newVersion = oldVersion + 1;

        // 写入新值
        metaObject.setValue("delegate.boundSql.parameterObject." + versionFieldName, newVersion);

        // 修改SQL语句
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        // 原始SQL
        String originalSql = boundSql.getSql();

        final Statement smt = CCJSqlParserUtil.parse(originalSql);
        if (!(smt instanceof Update)) {
            // 双重防护
            return invocation.proceed();
        }

        Update update = ((Update) smt);

        // set new version
        ArrayList<UpdateSet> updateSet = update.getUpdateSets();
        updateSet.add(new UpdateSet(new Column(versionField.getName()), new LongValue(newVersion)));

        // 添加查询条件
        final Expression originalWhere = update.getWhere();
        Expression newWhere = new EqualsTo(new Column(versionField.getName()), new LongValue(oldVersion));

        if (originalWhere != null) {
            newWhere = new AndExpression(originalWhere, newWhere);
        }
        update.setWhere(newWhere);
        originalSql = update.toString();

        // 覆盖为新的SQL
        metaObject.setValue("delegate.boundSql.sql", originalSql);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    public List<String> getIgnoreEntityList() {
        return ignoreEntityList;
    }

    public void setIgnoreEntityList(List<String> ignoreEntityList) {
        this.ignoreEntityList = ignoreEntityList;
    }
}