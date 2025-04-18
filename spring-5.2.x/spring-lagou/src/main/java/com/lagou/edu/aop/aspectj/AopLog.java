package com.lagou.edu.aop.aspectj;



import org.springframework.stereotype.Component;

/**
 * <p>
 * 使用 aop 切面记录请求日志信息
 * </p>
 *
 * @author yangkai.shen
 * @author chen qi
 * @date Created in 2018-10-01 22:05
 */
@Component
public class AopLog {
//    /**
//     * 切入点
//     */
//    @Pointcut("execution(public * com.xkcoding.log.aop.controller.*Controller.*(..))")
//    public void log() {
//
//    }

    public void beforeLog(){
        System.out.println("前置增强");
        //获取传入目标方法的参数
//        Object[] args = joinPoint.getArgs();
//        log.info("类名：{}", joinPoint.getSignature().getDeclaringType().getSimpleName());
//        log.info("方法名:{}", joinPoint.getSignature().getName() );
    }


    public void aroundLog() throws Throwable {
        System.out.println("环绕增强");
    }

    public void afterLog(){
        System.out.println("后置增强");
    }
}
