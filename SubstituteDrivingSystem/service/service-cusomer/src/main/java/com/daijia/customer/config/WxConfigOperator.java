package com.daijia.customer.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/5 9:57
 */
@Component
public class WxConfigOperator {

    @Resource
    private WxConfigProperties wxConfigProperties;

    @Bean
    private WxMaService wxMaService(){

        // 微信小程序id和密钥
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();

        wxMaConfig.setAppid(wxConfigProperties.getAppId());
        wxMaConfig.setSecret(wxConfigProperties.getSecret());

        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(wxMaConfig);
        return service;
    }
}