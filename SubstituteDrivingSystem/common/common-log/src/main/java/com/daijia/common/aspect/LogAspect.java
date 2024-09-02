package com.daijia.common.aspect;

import com.alibaba.fastjson.JSON;
import com.daiji.system.client.SysOperLogFeignClient;
import com.daijia.common.annoation.Log;
import com.daijia.common.util.IpUtil;
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
    private SysOperLogFeignClient sysOperLogFeignClient;

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
     * @param e
     * @param jsonResult
     */
    @SneakyThrows
    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes  sra =(ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        // *======数据库日志======//
        SysOperLog operationLog = new SysOperLog();
        operationLog.setStatus(1);

        // 请求的地址
        String ip = IpUtil.getIpAddress(request);
        operationLog.setOperIp(ip);
        operationLog.setOperUrl(request.getRequestURI());

        if (e!=null) {

            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
        }
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operationLog.setMethod(className + "."+ methodName+"()");

        // 设置请求方式
        operationLog.setRequestMethod(request.getMethod());
        // 处理设置注解上的参数
        getControllerMethodDescription(joinPoint,controllerLog,operationLog,jsonResult);

        // 保存数据库
        sysOperLogFeignClient.saveSysLog(operationLog);
        log.info("操作日志"+ JSON.toJSONString(operationLog));
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    private void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) {
    }

}
