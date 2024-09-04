package com.daijia.common.login;

import com.daijia.common.constant.RedisConstant;
import com.daijia.common.exeception.BusinessException;
import com.daijia.common.result.ResultCodeEnum;
import com.daijia.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/4 9:03
 */
@Component
@Aspect
public class LoginAspect {
    @Resource
    private RedisTemplate redisTemplate;

    @Around("execution(* com.daijia.*.controller.*.*(..)) && @annotation(Login)")
    public Object login(ProceedingJoinPoint proceedingJoinPoint,Login login) throws Throwable {

        //1 获取request对象
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)attributes;
        HttpServletRequest request = sra.getRequest();

        //2 从请求头获取token
        String token = request.getHeader("token");

        // 3,判断token是否为空，若为空，返回登陆提示
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCodeEnum.LOGIN_AUTH);
        }

        //4.token 不为空，查询redis
        String customerId = (String)redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX+token);

        // 查询redis对应用户Id,把用户Id放到ThreadLocal里面
        if (StringUtils.hasText(customerId)) {
            AuthContextHolder.setUserId(Long.parseLong(customerId));
        }
        //6.执行业务代码
        return  proceedingJoinPoint.proceed();
    }
}
