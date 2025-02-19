package com.atguigu.daijia.rules.service.impl;

import com.atguigu.daijia.model.form.rules.FeeRuleRequest;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponse;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import com.atguigu.daijia.rules.service.FeeRuleService;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/2/19 10:33
 */
public class FeeRuleServiceImpl implements FeeRuleService {

    @Resource
    private KieContainer kieContainer;

    //计算订单费用
    @Override
    public FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm calculateOrderFeeForm) {

        // 封装输入对象
        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();
        feeRuleRequest.setDistance(calculateOrderFeeForm.getDistance());
        Date startTime = calculateOrderFeeForm.getStartTime();
        feeRuleRequest.setStartTime(new DateTime(startTime).toString());
        feeRuleRequest.setWaitMinute(calculateOrderFeeForm.getWaitMinute());



        // 计算费用
        KieSession kieSession = kieContainer.newKieSession();

        // 封装返回对象
        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();
        kieSession.setGlobal("feeRuleResponse",feeRuleResponse);

        kieSession.insert(feeRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();

        FeeRuleResponseVo feeRuleResponseVo = new FeeRuleResponseVo();
        BeanUtils.copyProperties(feeRuleResponse, feeRuleResponseVo);

        return feeRuleResponseVo;
    }
}
