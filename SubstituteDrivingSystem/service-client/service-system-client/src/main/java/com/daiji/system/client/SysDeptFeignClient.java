package com.daiji.system.client;

import com.daijia.common.result.Result;
import com.daijia.model.entity.system.SysDept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/8/30 16:15
 */
@FeignClient(value = "service-system")
public interface SysDeptFeignClient {

    @GetMapping("/sysDept/getById/{id}")
    Result<SysDept> getById(@PathVariable Long id);

    @PostMapping("/sysDept/getById/{id}")
    Result<Boolean> save(@RequestBody SysDept sysDept);

    /**
     * 获取用户部门节点
     *
     * @return
     */
    @GetMapping("/sysDept/findUserNodes")
    Result<List<SysDept>> findUserNodes();

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @GetMapping("/sysDept/updateStatus/{id}/{status}")
    Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status);

}
