package com.daiji.system.client;

import com.daijia.common.result.Result;
import com.daijia.model.entity.system.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 产品列表API
 *
 * @author: 段世超
 * @aate: Created in 2024/8/29 17:57
 */
@FeignClient(value = "service-system")
public interface SecurityLoginFeignClient {

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    @GetMapping("/securityLogin/getByUsername/{username}")
    Result<SysUser> getByUsername(@PathVariable("username") String username);

    /**
     * 获取用户按钮权限
     *
     * @param userId
     * @return
     */

    Result<SysUser> findUserPermsList(@PathVariable("userId") Long userId);

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/securityLogin/getUserInfo/{userId}")
    Result<Map<String, Object>> getUserInfo(@PathVariable("userId") Long userId);
}
