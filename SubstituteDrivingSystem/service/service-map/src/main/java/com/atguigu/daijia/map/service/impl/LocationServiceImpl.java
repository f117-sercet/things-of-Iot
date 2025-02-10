package com.atguigu.daijia.map.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.repository.OrderServiceLocationRepository;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description： 地图、导航类service
 *
 * @author: 段世超
 * @aate: Created in 2025/2/10 11:40
 */
@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Autowired
    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Resource
    private OrderServiceLocationRepository orderServiceLocationRepository;

    @Resource
    private MongoTemplate mongoTemplate;
    //private DriverInfoFeignClient driverInfoFeignClient;


    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        return null;
    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        return null;
    }


    // 搜索附近满足条件的司机
    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {

        // 搜索经纬度位置5公里以内的司机
        // 1.操作redis里面geo
        //创建point,经纬度位置
        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(),
                searchNearByDriverForm.getLatitude().doubleValue());

        // 定义距离,5公里
        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS,
                RedisGeoCommands.DistanceUnit.KILOMETERS);
        // 创建circle对象、point distance
        Circle circle = new Circle(point, distance);
        // 定义GEO参数,设置返回结果包含内容
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                // 包含距离
                .includeDistance()
                // 包含坐标
                .includeCoordinates()
                // 升序
                .sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> result =
                redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);

        // 2.查询redis最终返回的list集合
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();
        // 3.对查询list集合进行处理
        // 遍历list集合,得到每个司机的信息
        // 根据每个司机个性化设置信息判断
        ArrayList<NearByDriverVo> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(content)) {
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();

            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

                // 获取司机Id
                long driverId = Long.parseLong(item.getContent().getName());

                // 远程调用,根据司机id个性化设置信息
                Result<DriverSet> driverSetResult = driverInfoFeignClient.getDriverSet(driverId);
                DriverSet driverSet = driverSetResult.getData();

                // 判断订单里程order_distance
                BigDecimal orderDistance = driverSet.getOrderDistance();

                // orderDistance ==0,司机没有限制
                // 如果不等于0,比如30,接单30公里代驾订单
                // 接单距离-当前单子距离 <0,不复合条件
                if (orderDistance.doubleValue()!=0 && orderDistance.subtract(searchNearByDriverForm.getMileageDistance()).doubleValue()<0) {
                    continue;
                }
                // 判断接单里程 accept_distance
                // 当前接单距离
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal acceptDistance = driverSet.getAcceptDistance();

                if (acceptDistance.doubleValue() !=0 && acceptDistance.subtract(currentDistance).doubleValue()<0) {

                    continue;
                }
                // 封装符合条件的数据
                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);
            }
        }
        return list;
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        return null;
    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        return null;
    }

    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList) {
        return null;
    }

    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {
        return null;
    }

    @Override
    public BigDecimal calculateOrderRealDistance(Long orderId) {
        return null;
    }
}
