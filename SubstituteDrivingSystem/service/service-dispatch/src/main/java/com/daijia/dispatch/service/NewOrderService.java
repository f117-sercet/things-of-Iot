package com.daijia.dispatch.service;

import com.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.daijia.model.vo.order.NewOrderDataVo;

import java.util.List;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/22 10:49
 */
public interface NewOrderService {

    // 创建并启动任务调度方法
    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    // 执行任务:搜索附近代驾司机
    void executeTask(long jobId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    Boolean clearNewOrderQueueData(Long driverId);
}
