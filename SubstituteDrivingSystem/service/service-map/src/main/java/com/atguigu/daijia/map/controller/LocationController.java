package com.atguigu.daijia.map.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Description：位置API接口管理
 *
 * @author: 段世超
 * @aate: Created in 2025/2/10 14:15
 */
@Slf4j
@Tag(name = "位置API接口管理")
@RequestMapping("/map/location")
public class LocationController {

    @Resource
    private LocationService locationService;

    public Result<List<NearByDriverVo>> searchNearByDriver(
            @RequestBody SearchNearByDriverForm searchNearByDriverForm
            ){
        List<NearByDriverVo> nearByDriverVos = locationService.searchNearByDriver(searchNearByDriverForm);

        return Result.ok(nearByDriverVos);
    }
}
