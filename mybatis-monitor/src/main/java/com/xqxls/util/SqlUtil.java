package com.xqxls.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/8 15:40
 */
public class SqlUtil {


    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 获取完整的sql实体的信息
     * @param boundSql boundSql
     * @param configuration 配置
     * @return 处理后的sql
     */
    public static String formatSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        // 输入sql字符串空判断
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        if (configuration == null) {
            return "";
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        sql = beautifySql(sql);
        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    String paramValueStr = "";
                    if (value instanceof String) {
                        paramValueStr = "'" + value + "'";
                    } else if (value instanceof Date) {
                        paramValueStr = "'" + DATE_FORMAT_THREAD_LOCAL.get().format(value) + "'";
                    } else {
                        paramValueStr = value + "";
                    }
                    sql = sql.replaceFirst("\\?", paramValueStr);
                }
            }
        }
        return sql;
    }

    public static String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }

}
