package com.ai.usersentiments.infrastructure.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Before("execution(* com.ai..*.*(..))")
    public void beforeMethod(JoinPoint jp) {
        log.debug("Entering: {} with args {}", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(value = "execution(* com.ai..*.*(..))", returning = "output")
    public void afterMethod(JoinPoint jp, Object output) {
        log.debug("Exiting: {} with output {}", jp.getSignature(), output);
    }
}
