package com.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daijia.customer.mapper.CustomerInfoMapper;
import com.daijia.customer.mapper.CustomerLoginLogMapper;
import com.daijia.customer.service.CustomerInfoService;
import com.daijia.model.entity.customer.CustomerInfo;
import com.daijia.model.entity.customer.CustomerLoginLog;
import com.daijia.model.form.customer.UpdateWxPhoneForm;
import com.daijia.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/5 10:07
 */
@Service
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Resource
    private CustomerLoginLogMapper customerLoginLogMapper;

    @Override
    @SneakyThrows
    public Long login(String code) {

        // 获取code值,使用微信工具包,获取微信唯一标识openId
        String openId =null;
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        openId = sessionInfo.getOpenid();

        // 2.根据openId查询数据库，判断是否第一次登录
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getWxOpenId,openId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(wrapper);
        //3 如果第一次登录，添加信息到用户表
        if (customerInfo ==null){
            customerInfo = new CustomerInfo();
            customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));

            customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            customerInfo.setWxOpenId(openId);
            customerInfoMapper.insert(customerInfo);
        }
        //4 记录登录日志信息
        CustomerLoginLog customerLoginLog = new CustomerLoginLog();
        customerLoginLog.setCustomerId(customerInfo.getId());
        customerLoginLog.setMsg("小程序登录");
        customerLoginLogMapper.insert(customerLoginLog);

        //5 返回用户id
        return customerInfo.getId();
    }

    @Override
    public CustomerLoginVo getCustomerInfo(Long customerId) {
        return null;
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        return null;
    }

    @Override
    public String getCustomerOpenId(Long customerId) {
        return "";
    }
}
