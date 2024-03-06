package com.xqxls.annotation;

import com.xqxls.enums.Operation;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 17:14
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Watch {

    /** 操作类型 **/
    Operation operation() default Operation.SELECT;

    /** 备注 **/
    String remark() default "";

}
