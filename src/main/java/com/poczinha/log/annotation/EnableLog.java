package com.poczinha.log.annotation;

import com.poczinha.log.bean.LogConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Import(LogConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableLog {
    String headerName();
    boolean logOnlyIfPresent() default true;
}
