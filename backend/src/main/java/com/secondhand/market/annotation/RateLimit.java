package com.secondhand.market.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 用于标记需要限流的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 时间窗口内最大请求次数
     */
    int count() default 100;

    /**
     * 时间窗口（秒）
     */
    int window() default 60;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后再试";
}