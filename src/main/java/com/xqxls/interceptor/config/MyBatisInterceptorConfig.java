package com.xqxls.interceptor.config;

import com.xqxls.interceptor.MyBatisInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/8 13:41
 */
@Configuration
public class MyBatisInterceptorConfig {

    @Bean
    public String myInterceptor(SqlSessionFactory sqlSessionFactory) {
        //实例化插件
        MyBatisInterceptor sqlInterceptor = new MyBatisInterceptor();
        //将插件添加到SqlSessionFactory工厂
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
        return "interceptor";
    }
}
