package com.atguigu.daijia.dispatch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.dispatch.mapper.OrderJobMapper;
import com.atguigu.daijia.dispatch.service.NewOrderService;
import com.atguigu.daijia.dispatch.xxl.client.XxlJobClient;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.dispatch.OrderJob;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;

    @Resource
    private XxlJobClient xxlJobClient;

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        //1.判断当前订单是否启动任务调度
        LambdaQueryWrapper<OrderJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId());
        OrderJob orderJob = orderJobMapper.selectOne(wrapper);

        //2.没有启动进行操作
        if (orderJob==null) {
            // 创建并启动任务调度
            // String executorHandler 执行任务 job方法
            // String param
            // String corn 执行corn 表达式
            // String desc 描述信息
            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler", "",
                    "0 0/1 * * * ?",
                    "新创建订单任务调度：" + newOrderTaskVo.getOrderId());

            //3.记录任务调度信息
            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));
            orderJobMapper.insert(orderJob);
        }
        return orderJob.getJobId();
    }

    @Override
    public void executeTask(long jobId) {

        //1.根据JobId查询数据库，当前任务是否已经创建
        // 如果没有创建，不往下执行
        LambdaQueryWrapper<OrderJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderJob::getOrderId,jobId);
        OrderJob orderJob = orderJobMapper.selectOne(wrapper);
        if (orderJob ==null) {

            return;
        }
        //2.查询订单状态，如果当前订单接单，继续执行。如果当前订单不是接单状态，停止任务调度
        //3.远程调用:如果附近满足条件可以接单司机
        //4.远程调用之后，获取满足可以接单的司机集合
        //5 遍历司机集合，得到每个司机，为每个司机创建临时队列，存储新订单信息

    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        //以自己的意志抵达结局吧~
        return List.of();
    }

    @Override
    public Boolean clearNewOrderQueueData(Long driverId) {
        return null;
    }
}
