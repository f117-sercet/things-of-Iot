package com.atguigu.daijia.map.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.map.service.MapService;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Description： 地图实现类
 *
 * @author: 段世超
 * @aate: Created in 2025/2/13 11:01
 */
@Slf4j
@Service
public class MapServiceImpl implements MapService {

    @Resource
    private RestTemplate restTemplate;

    @Value("${tencent.map.key")
    private String key;

    //计算驾驶路线
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {

        // 请求接口，返回相关结果
        String url = "https://apis.map.qq.com/ws/direction/v1/driving/?from={from}&to={to}&key={key}";

        // 封装传递参数
        HashMap<Object, Object> map = new HashMap<>();

        // 开始位置
        //经纬度
        map.put("from",calculateDrivingLineForm.getStartPointLongitude());
        map.put("to",calculateDrivingLineForm.getEndPointLatitude());

        // key
        map.put("key",key);

        // 使用RestTemplate调用 GET
        JSONObject result = restTemplate.getForObject(url, JSONObject.class, map);

        // 处理返回结果
        int status = result.getIntValue("status");
        // 判断调用是否成功
        if (status!=0) {
            throw new GuiguException(ResultCodeEnum.MAP_FAIL);
        }
        // 获取返回路线信息
        JSONObject route = result.getJSONObject("result").getJSONArray("routes").getJSONObject(0);

        // 处理返回结果
        DrivingLineVo drivingLineVo = new DrivingLineVo();
        drivingLineVo.setDuration(route.getBigDecimal("duration"));
        drivingLineVo.setDistance(route.getBigDecimal("distance")
                .divide(new BigDecimal(1000))
                .setScale(2, RoundingMode.HALF_UP));

        // 路线
        drivingLineVo.setPolyline(route.getJSONArray("polyline"));

        return drivingLineVo;
    }
}
