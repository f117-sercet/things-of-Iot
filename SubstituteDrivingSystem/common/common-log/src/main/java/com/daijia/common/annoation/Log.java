package com.daijia.common.annoation;

import com.daijia.common.enums.BusinessType;
import com.daijia.common.enums.OperatorType;

import java.lang.annotation.*;

/**
 * Description： 自定义操作记录注解
 *
 * @author: 段世超
 * @aate: Created in 2024/8/22 17:04
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块
     */
    public String title() default "";

    /**
     * 功能
     */
    public BusinessType businessType() default  BusinessType.OTHER;

    /**
     * 是否保存请求的参数
     * @return
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;

}
