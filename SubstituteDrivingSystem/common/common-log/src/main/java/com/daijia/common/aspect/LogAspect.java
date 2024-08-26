package com.daijia.common.aspect;

import com.daijia.common.annoation.Log;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult){

    }

}
