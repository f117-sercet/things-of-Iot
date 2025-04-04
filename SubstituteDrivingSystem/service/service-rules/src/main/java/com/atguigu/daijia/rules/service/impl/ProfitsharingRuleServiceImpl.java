package com.atguigu.daijia.rules.service.impl;

import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequest;
import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponse;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponseVo;
import com.atguigu.daijia.rules.mapper.ProfitsharingRuleMapper;
import com.atguigu.daijia.rules.service.ProfitsharingRuleService;
import com.atguigu.daijia.rules.utils.DroolsHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/2/26 17:40
 */
@Slf4j
@Service
public class ProfitsharingRuleServiceImpl implements ProfitsharingRuleService {

    @Resource
    private ProfitsharingRuleMapper rewardRuleMapper;

    private static final String RULES_CUSTOMER_RULES_DRL = "rules/ProfitsharingRule.drl";

    @Override
    public ProfitsharingRuleResponseVo calculateOrderProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm) {

        // 传入参对象封装
        ProfitsharingRuleRequest profitsharingRuleRequest = new ProfitsharingRuleRequest();
        profitsharingRuleRequest.setOrderAmount(profitsharingRuleRequestForm.getOrderAmount());
        profitsharingRuleRequest.setOrderNum(profitsharingRuleRequestForm.getOrderNum());


        // 创建kieSession

        KieSession kieSession = DroolsHelper.loadForRule(RULES_CUSTOMER_RULES_DRL);

        // 封装返回对象
        ProfitsharingRuleResponse profitsharingRuleResponse = new ProfitsharingRuleResponse();
        kieSession.setGlobal("profitsharingRuleResponse",profitsharingRuleResponse);
        // 触发规则,返回VO对象
        kieSession.insert(profitsharingRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();

        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = new ProfitsharingRuleResponseVo();
        BeanUtils.copyProperties(profitsharingRuleResponse,profitsharingRuleResponseVo);

        return profitsharingRuleResponseVo;
    }
}
