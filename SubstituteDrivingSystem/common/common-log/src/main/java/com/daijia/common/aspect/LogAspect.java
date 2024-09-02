package com.daijia.common.aspect;

import com.alibaba.fastjson.JSON;
import com.daiji.system.client.SysOperLogFeignClient;
import com.daijia.common.annoation.Log;
import com.daijia.common.util.IpUtil;
import com.daijia.model.entity.system.SysOperLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;

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

        // 设置action动作
        operLog.setBusinessType(log.businessType().name());
        // 设置标题
        operLog.setTitle(log.title());

        // 设置操作人员类别
        operLog.setOperatorType(log.operatorType().name());

        // 是否需要保存request,参数和值
        if (log.isSaveRequestData()) {

            // 获取参数的信息,并且传入到数据库中
            setRequestValue(joinPoint,operLog);
        }
        // 是否需要保存response,参数和值
        if (log.isSaveRequestData() && null!=jsonResult) {
            operLog.setJsonResult(JSON.toJSONString(jsonResult));
        }
    }

    /**
     * 获取请求参数,放到log中
     * @param joinPoint
     * @param operLog
     */

    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) {

        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            
            String params = argsArrayToString(joinPoint.getArgs());
            
            operLog.setOperParam(params);
            
            
        }
    }

    /**
     * 参数拼接
     * @param paramsArray
     * @return
     */
    @SneakyThrows
    private String argsArrayToString(Object[] paramsArray) {

        String params = "";

        if (paramsArray!=null && paramsArray.length >0) {
            for (Object o : paramsArray) {
                if (null!=o && !isFilterObject(o)) {
                    Object jsonObj = JSON.toJSON(o);
                    params+=jsonObj.toString()+"";
                }
            }
        }
        return params.trim();
    }

    private boolean isFilterObject( final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {

            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }

        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map)o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}
