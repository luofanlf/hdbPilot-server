package com.iss.hdbPilot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//表示这个注解可以用于方法上
@Target(ElementType.METHOD)
//表示会在运行时被jvm保留，只有加了 RUNTIME 级别，反射才能读取到这个注解并实现权限检查逻辑。
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     */
    String mustRole() default "";
}
