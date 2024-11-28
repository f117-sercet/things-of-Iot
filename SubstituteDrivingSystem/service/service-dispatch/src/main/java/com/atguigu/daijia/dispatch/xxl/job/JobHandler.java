package com.atguigu.daijia.dispatch.xxl.job;

import com.atguigu.daijia.dispatch.mapper.XxlJobLogMapper;
import com.atguigu.daijia.dispatch.service.NewOrderService;
import com.atguigu.daijia.model.entity.dispatch.XxlJobLog;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/28 9:50
 */
@Component
public class JobHandler {

    @Resource
    private XxlJobLogMapper xxlJobLogMapper;

    @Resource
    private NewOrderService newOrderService;

    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler(){
        XxlJobLog xxlJobLog = new XxlJobLog();
        xxlJobLog.setJobId(XxlJobHelper.getJobId());
        long startTime = System.currentTimeMillis();
        try {
            //执行任务:搜索附近代驾司机
            newOrderService.executeTask(XxlJobHelper.getJobId());
            // 成功状态
            xxlJobLog.setStatus(1);
        }catch (Exception e){

            // 失败状态
            xxlJobLog.setStatus(0);
            xxlJobLog.setError(e.getMessage());
            e.printStackTrace();
        }finally {
            long times = System.currentTimeMillis() - startTime;
            // TODO 完善long
            xxlJobLog.setTimes((int) times);
            xxlJobLogMapper.insert(xxlJobLog);
        }
    }
}
