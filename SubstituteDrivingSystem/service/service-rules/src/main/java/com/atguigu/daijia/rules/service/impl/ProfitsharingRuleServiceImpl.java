package com.atguigu.daijia.rules.service.impl;

import com.atguigu.daijia.model.form.rules.ProfitsharingRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.ProfitsharingRuleResponseVo;
import com.atguigu.daijia.rules.mapper.ProfitsharingRuleMapper;
import com.atguigu.daijia.rules.service.ProfitsharingRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

        // 创建kieSession

        // 封装返回对象

        // 触发规则,返回VO对象

        return null;
    }
}
