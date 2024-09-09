package com.daijia.customer.service;

import com.daijia.model.entity.customer.CustomerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daijia.model.form.customer.UpdateWxPhoneForm;
import com.daijia.model.vo.customer.CustomerLoginVo;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/5 9:18
 */
public interface CustomerInfoService extends IService<CustomerInfo> {

    // 微信向程序登录接口
    Long login(String code);

    // 获取客户登录信息
    CustomerLoginVo getCustomerInfo(Long customerId);

    // 更新客户端微信手机号
    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);

    String getCustomerOpenId(Long customerId);
}
