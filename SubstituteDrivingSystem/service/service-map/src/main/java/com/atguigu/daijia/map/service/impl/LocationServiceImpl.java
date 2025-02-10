package com.atguigu.daijia.map.service.impl;

import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.repository.OrderServiceLocationRepository;
import com.atguigu.daijia.map.service.LocationService;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description： TODO
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

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Resource
    private OrderServiceLocationRepository orderServiceLocationRepository;

    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        return null;
    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        return null;
    }

    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {
        return List.of();
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
