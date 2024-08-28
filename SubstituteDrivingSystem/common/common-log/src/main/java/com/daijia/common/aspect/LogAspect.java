package com.daijia.common.aspect;

import com.daijia.common.annoation.Log;
import com.daijia.model.entity.system.SysOperLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/8/26 18:19
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    // 微服务切换为feign调用接口
    @Resource
    private SysOperaLogFeignClient sysOperaLogFeignClient;

    /***
     * 完成请求后执行
     * @param joinPoint
     * @param controllerLog
     * @param jsonResult
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)",returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult){
        handleLog(joinPoint, controllerLog, null, jsonResult);
        
    }

    /**
     * 拦截异常操作
     * @param joinPoint
     * @param controllerLog
     * @param o
     * @param jsonResult
     */
    @SneakyThrows
    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, Object o, Object jsonResult) {

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes  sra =(ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        // *======数据库日志======//
        new SysOperLog();

    }

}
