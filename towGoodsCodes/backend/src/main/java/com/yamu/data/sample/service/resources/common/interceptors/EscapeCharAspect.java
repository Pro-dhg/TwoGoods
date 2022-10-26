package com.yamu.data.sample.service.resources.common.interceptors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EscapeCharAspect {

    @Pointcut("execution(* com.yamu.data.sample.service.resources.controller..*(..))")
    public void escapeChar() {}

    @Before("escapeChar()")
    public void before(JoinPoint joinPoint) throws NoSuchMethodException {
        AspectEscapeHandler.escape(joinPoint);
    }
}
