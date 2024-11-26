package com.daijia.dispatch.service.impl;

import com.daijia.dispatch.mapper.OrderJobMapper;
import com.daijia.dispatch.service.NewOrderService;
import com.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.daijia.model.vo.order.NewOrderDataVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/22 10:53
 */
@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Autowired
    private OrderJobMapper orderJobMapper;

    @Resource
    private XxlJobClient xxlJobClient;

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        return Long.parseLong("1");
    }

    @Override
    public void executeTask(long jobId) {

    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        return List.of();
    }

    @Override
    public Boolean clearNewOrderQueueData(Long driverId) {
        return null;
    }
}
