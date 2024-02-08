package com.xqxls.interceptor;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.AsyncEventBus;
import com.xqxls.enums.Operation;
import com.xqxls.event.Message;
import com.xqxls.interceptor.constant.Constant;
import com.xqxls.po.LogInfo;
import com.xqxls.util.SpringUtil;
import com.xqxls.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.time.LocalDateTime;
import java.util.Properties;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/8 13:17
 */

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class})
})
@Slf4j
public class MyBatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        Configuration configuration = mappedStatement.getConfiguration();
        Object target = invocation.getTarget();
        StatementHandler handler = configuration.newStatementHandler((Executor) target, mappedStatement,
                parameter, RowBounds.DEFAULT, null, null);
        //记录耗时
        long start = System.currentTimeMillis();
        //执行真正的方法
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        //记录时间
        long spendTime = end - start;
        //获取执行方法的位置
        String namespaceId = mappedStatement.getId();
        //获取mapper名称
        String namespace = namespaceId.substring(0, namespaceId.lastIndexOf("."));
        if(namespace.equals(Constant.LOG_INFO_NAMESPACE)){
            return result;
        }
        //获取方法名
        String methodName = namespaceId.substring(namespaceId.lastIndexOf(".") + 1);
        //获取方法类型
        String type = buildMethodType(mappedStatement);
        //记录SQL
        BoundSql boundSql = handler.getBoundSql();
        String executeSql = SqlUtil.beautifySql(boundSql.getSql());
        String finalSql = SqlUtil.formatSql(boundSql,configuration);
        LogInfo logInfo = buildLogInfo(namespace, methodName, type, executeSql, finalSql, parameter, result, spendTime);
        sendMessage(logInfo);
        return result;
    }

    private static String buildMethodType(MappedStatement mappedStatement) {
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String type;
        switch(sqlCommandType){
            case INSERT :
                type = Operation.INSERT.getDesc();
                break;
            case UPDATE:
                type = Operation.UPDATE.getDesc();
                break;
            case DELETE:
                type = Operation.DELETE.getDesc();
                break;
            case SELECT:
                type = Operation.SELECT.getDesc();
                break;
            default :
                type = Operation.UNKNOWN.getDesc();
        }
        return type;
    }

    private void sendMessage(LogInfo logInfo) {
        Message message = new Message(logInfo);
        log.info("发送消息，message：{}", JSON.toJSON(message));
        AsyncEventBus eventBus = SpringUtil.getBean(AsyncEventBus.class);
        eventBus.post(message);
    }

    private LogInfo buildLogInfo(String namespace, String methodName, String type, String executeSql, String finalSql, Object parameter, Object result, long spendTime) {
        LogInfo logInfo = LogInfo.builder()
                .namespace(namespace)
                .methodName(methodName)
                .type(type)
                .executeSql(executeSql)
                .finalSql(finalSql)
                .parameter(parameter !=null?JSON.toJSONString(parameter):null)
                .returnValue(result !=null?JSON.toJSONString(result):null)
                .spendTime(spendTime)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        log.info("logInfo is " + JSON.toJSON(logInfo));
        return logInfo;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
