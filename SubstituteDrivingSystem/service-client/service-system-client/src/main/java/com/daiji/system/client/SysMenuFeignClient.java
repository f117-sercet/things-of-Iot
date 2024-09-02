package com.daiji.system.client;

import com.daijia.common.result.Result;
import com.daijia.model.entity.system.SysMenu;
import com.daijia.model.vo.system.AssginMenuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/2 9:05
 */
@FeignClient(value = "service-system")
public interface SysMenuFeignClient {

    /**
     * 获取菜单
     */

    @GetMapping("/sysMenu/findNodes")
    Result<List<SysMenu>> findNodes();

    @PostMapping("/sysMenu/save")
    Result<Boolean> save(@RequestBody SysMenu sysMenu);

    @PutMapping("/sysMenu/update")
    Result<Boolean> update(@RequestBody SysMenu permission);

    @DeleteMapping("/sysMenu/remove/{id}")
    Result<Boolean> remove(@PathVariable Long id);


    /***
     * 根据角色获取菜单
     */
    @GetMapping("/sysMenu/ToAssign/{roleId}")
    Result<List<SysMenu>> toAssign(@PathVariable Long roleId);

    /**
     * 给角色分配权限
     *
     * @param assginMenuVo
     * @return
     */
    @PostMapping("/sysMenu/doAssign")
    Result<Boolean> doAssign(@RequestBody AssginMenuVo assginMenuVo);

}
