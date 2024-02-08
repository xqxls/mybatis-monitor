package com.xqxls.aspect;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.AsyncEventBus;
import com.xqxls.annotation.Watch;
import com.xqxls.dao.ILogInfoDao;
import com.xqxls.event.Message;
import com.xqxls.po.LogInfo;
import com.xqxls.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.BoundSql;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
    public void doAfter(JoinPoint joinPoint,Object returnValue) throws IllegalAccessException {
        log.info("开始进入切面==============================");
        // 获取该方法上的 @Watch注解
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        if(clazz.equals(ILogInfoDao.class)){
            return;
        }
        Watch watch = buildWatch(method);
        if (watch == null) return;
        long spendTime = System.currentTimeMillis() - startTime.get();
        log.info("spendTime is " + spendTime);
        startTime.remove();
        //类路径
        String namespace = method.getDeclaringClass().getName();
        //方法名
        String methodName = method.getName();
        //入参
        Map<String, Object> parameterMap = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            parameterMap.put(object.getClass().getSimpleName(),object);
        }
        Configuration configuration = sqlSessionFactory.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(
                namespace + "." + methodName);
        //sql
        Map<String, Object> map = buildParameterObject(method, args);
        BoundSql boundSql = mappedStatement.getBoundSql(map);
        String sql = SqlUtil.beautifySql(boundSql.getSql());
        String finalSql = SqlUtil.formatSql(boundSql,configuration);
        LogInfo logInfo = buildLogInfo(returnValue, namespace, methodName, watch, sql, finalSql, parameterMap, spendTime);
        sendMessage(logInfo);
    }

    private void sendMessage(LogInfo logInfo) {
        Message message = new Message(logInfo);
        log.info("发送消息，message：{}", JSON.toJSON(message));
        eventBus.post(message);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> buildParameterObject(Method method, Object[] args) throws IllegalAccessException {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Map<String,Object> map = new HashMap<>();
        for (int i = 0;i<parameterAnnotations.length;i++){
            Object object = args[i];
            if (parameterAnnotations[i].length == 0){
                //说明该参数没有注解，此时该参数可能是实体类，也可能是Map，也可能只是单参数
                if (object.getClass().getClassLoader() == null && object instanceof Map){
                    map.putAll((Map<? extends String, ?>) object);
                    log.info("该对象为Map");
                }
                else{
                    //形参为自定义实体类
                    map.putAll(objectToMap(object));
                    log.info("该对象为用户自定义的对象");
                }
            }else{
                //说明该参数有注解，且必须为@Param
                for (Annotation annotation : parameterAnnotations[i]){
                    if (annotation instanceof Param){
                        map.put(((Param) annotation).value(),object);
                    }
                }
            }
        }
        return map;
    }

    private Watch buildWatch(Method method) {
        return (Watch) Arrays.stream(method.getDeclaredAnnotations())
                .filter(a -> a instanceof Watch)
                .findFirst()
                .orElse(null);
    }

    private LogInfo buildLogInfo(Object returnValue, String namespace, String methodName, Watch watch, String sql, String finalSql,Map<String, Object> parameterMap, long spendTime) {
        LogInfo logInfo = LogInfo.builder()
                .namespace(namespace)
                .methodName(methodName)
                .type(watch.operation().getDesc())
                .executeSql(sql)
                .finalSql(finalSql)
                .parameter(!parameterMap.isEmpty()?JSON.toJSONString(parameterMap):null)
                .returnValue(returnValue !=null?JSON.toJSONString(returnValue):null)
                .spendTime(spendTime)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        log.info("logInfo is " + JSON.toJSON(logInfo));
        return logInfo;
    }

    /**
     * 获取利用反射获取类里面的值和名称
     * @param obj 待处理对象
     * @return 参数集合
     */
    private static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        log.info("clazz is " + clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }

}
