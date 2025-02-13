package com.atguigu.daijia.map.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.LocationUtil;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.repository.OrderServiceLocationRepository;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
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
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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


    // 更新司机位置
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {

        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(),
                updateDriverLocationForm.getLatitude().doubleValue());

        // 添加到redis 中
        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_LOCATION,
                point,
                updateDriverLocationForm.getDriverId().toString());

        return true;
    }

    // 删除司机位置信息
    @Override
    public Boolean removeDriverLocation(Long driverId) {

        redisTemplate.opsForGeo().remove(
                RedisConstant.DRIVER_GEO_LOCATION,
                driverId.toString()
        );

        return true;
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
                if (orderDistance.doubleValue() != 0 && orderDistance.subtract(searchNearByDriverForm.getMileageDistance()).doubleValue() < 0) {
                    continue;
                }
                // 判断接单里程 accept_distance
                // 当前接单距离
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal acceptDistance = driverSet.getAcceptDistance();

                if (acceptDistance.doubleValue() != 0 && acceptDistance.subtract(currentDistance).doubleValue() < 0) {

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

    // 更新订单地址到缓存
    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {

        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLongitude(updateOrderLocationForm.getLongitude());
        orderLocationVo.setLatitude(updateOrderLocationForm.getLatitude());
        String key = RedisConstant.UPDATE_ORDER_LOCATION + updateOrderLocationForm.getOrderId();
        ;

        redisTemplate.opsForValue().set(key, orderLocationVo);
        return true;
    }

    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {

        String key = RedisConstant.UPDATE_ORDER_LOCATION + orderId;
        OrderLocationVo orderLocationVo = (OrderLocationVo) redisTemplate.opsForValue().get(key);

        return orderLocationVo;
    }

    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList) {

        List<OrderServiceLocation> list = new ArrayList<>();

        // orderServiceLocation
        orderLocationServiceFormList.forEach(orderServiceLocationForm -> {
            OrderServiceLocation orderServiceLocation = new OrderServiceLocation();
            BeanUtils.copyProperties(orderServiceLocationForm, orderServiceLocation);
            orderServiceLocation.setId(ObjectId.get().toString());
            orderServiceLocation.setCreateTime(new Date());

            list.add(orderServiceLocation);
        });
        // 批量添加到MongoDB
        orderServiceLocationRepository.saveAll(list);


        return true;
    }

    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {

        // 查询MongoDB
        // 查询条件:orderId
        // 根据创建时间降序排列
        // 最新一条数据

        Query query = new Query();
        query.addCriteria(Criteria.where("orderId").is(orderId))
                .with(Sort.by(Sort.Order.desc("creatTime")))
                .limit(1);
        OrderServiceLocation orderServiceLocation = mongoTemplate.findOne(query, OrderServiceLocation.class);
        OrderServiceLastLocationVo orderServiceLastLocationVo = new OrderServiceLastLocationVo();
        BeanUtils.copyProperties(orderServiceLocation, orderServiceLastLocationVo);

        return orderServiceLastLocationVo;
    }

    @Override
    public BigDecimal calculateOrderRealDistance(Long orderId) {

        // 1.根据订单orderId查询list
        List<OrderServiceLocation> list = orderServiceLocationRepository.findByOrderIdByCreateTimeAsc(orderId);

        // 2.第一步查询返回订单位置信息
        double realDistance = 0;
        if (!CollectionUtils.isEmpty(list)) {
            for (int i = 0, size = list.size() - 1; i < size; i++) {

                OrderServiceLocation location1 = list.get(i);
                OrderServiceLocation location2 = list.get(i + 1);

                // 计算位置距离
                double distance = LocationUtil.getDistance(location1.getLatitude().doubleValue(),
                        location1.getLongitude().doubleValue(),
                        location2.getLatitude().doubleValue(),
                        location2.getLongitude().doubleValue());
                realDistance += distance;
            }
        }
        //TODO 测试用
        if (realDistance == 0) {
            return orderInfoFeignClient.getOrderInfo(orderId).getData().getExpectDistance().add(new BigDecimal("5"));
        }

        // 3.返回最终计算的实际距离


        return  new BigDecimal(realDistance);
    }
    }

