package com.per.msa_common.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggerAspect {
    protected Log log = LogFactory.getLog(LoggerAspect.class);
    static String name = "";
    static String type = "";

    @Around("execution(* com..*Controller.*(..)) or execution(* com..*Service.*(..)) or execution(* com..*DAO.*(..))")
    public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
        type = joinPoint.getSignature().getDeclaringTypeName();

        if (type.indexOf("Controller") > -1) {
            name = "Controller  \t:  ";
        } else if (type.indexOf("Service") > -1) {
            name = "Service  \t:  ";
        } else if (type.indexOf("DAO") > -1) {
            name = "DAO  \t\t:  ";
        }
        log.info(name + type + "." + joinPoint.getSignature().getName() + "()");
        return joinPoint.proceed();
    }
}
