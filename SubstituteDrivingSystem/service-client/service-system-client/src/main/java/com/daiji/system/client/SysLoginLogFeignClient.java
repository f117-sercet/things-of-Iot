package com.daiji.system.client;

import com.daijia.common.result.Result;
import com.daijia.model.entity.system.SysLoginLog;
import com.daijia.model.query.system.SysLoginLogQuery;
import com.daijia.model.vo.base.PageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Description： 产品API接口
 *
 * @author: 段世超
 * @create: Created in 2024/9/2 8:42
 */
@FeignClient(value = "service-system")
public interface SysLoginLogFeignClient {

    @PostMapping("/sysLoginLog/findPage/{page}/{limit}")
    Result<PageVo<SysLoginLog>> findPage(
            @PathVariable("page") Long page,
            @PathVariable("limit") Long limit,
            @RequestBody SysLoginLogQuery sysLoginLogQuery);

    @GetMapping("/sysLoginLog/getById/{id}")
    Result<SysLoginLog> getById(@PathVariable Long id);


    /**
     * 记录登录日志
     * @return
     */
    @PostMapping("/sysLoginLog/recordLoginLog")
    Result<Boolean> recordLoginLog(@RequestBody SysLoginLog sysLoginLog );

}