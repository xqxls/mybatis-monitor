package com.xqxls.aspect;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.AsyncEventBus;
import com.xqxls.annotation.Watch;
import com.xqxls.dao.ILogInfoDao;
import com.xqxls.event.Message;
import com.xqxls.po.LogInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 17:19
 */
@Slf4j
@Aspect
@Component
public class WatchAspect {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private AsyncEventBus eventBus;

    private final static ThreadLocal<Long> startTime = new ThreadLocal<>();


    /** 注解切面 **/
    @Pointcut("@annotation(com.xqxls.annotation.Watch)")
    public void pointCut(){}

//    @Pointcut("execution(public * com.xqxls.dao..*.*(..))")
//    public void pointCut(){}

    @Before(value = "pointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        startTime.set(System.currentTimeMillis());
    }

    /**
     * 拦截dao方法，记录操作数据
     * @param joinPoint 切入点
     * @param returnValue 返回值
     */
    @AfterReturning(value = "pointCut()",returning="returnValue")
    public void doAfter(JoinPoint joinPoint,Object returnValue) {
        log.info("开始进入切面==============================");
        // 获取该方法上的 @Watch注解
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        if(clazz.equals(ILogInfoDao.class)){
            return;
        }
        Watch watch = (Watch) Arrays.stream(method.getDeclaredAnnotations())
                .filter(a -> a instanceof Watch)
                .findFirst()
                .orElse(null);
        if(watch == null){
            return;
        }

        long spendTime = System.currentTimeMillis() - startTime.get();
        log.info("spendTime is " + spendTime);
        startTime.remove();

        //类路径
        String namespace = method.getDeclaringClass().getName();
        //方法名
        String methodName = method.getName();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(
                namespace + "." + methodName);
        //sql
        String sql = mappedStatement.getBoundSql(null).getSql();
        //入参
        Map<String, Object> parameterMap = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            parameterMap.put(object.getClass().getSimpleName(),object);
        }

        LogInfo logInfo = LogInfo.builder()
                .methodName(methodName)
                .type(watch.operation().getDesc())
                .executeSql(sql)
                .parameter(!parameterMap.isEmpty()?JSON.toJSONString(parameterMap):null)
                .returnValue(returnValue!=null?JSON.toJSONString(returnValue):null)
                .spendTime(spendTime)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        log.info("logInfo is " + JSON.toJSON(logInfo));
        Message message = new Message(logInfo);
        log.info("发送消息，message：{}", JSON.toJSON(message));
        eventBus.post(message);
    }
}
