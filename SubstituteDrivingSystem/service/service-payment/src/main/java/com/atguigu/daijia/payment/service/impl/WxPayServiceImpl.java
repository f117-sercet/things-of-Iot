package com.atguigu.daijia.payment.service.impl;

import com.atguigu.daijia.common.service.RabbitService;
import com.atguigu.daijia.model.entity.payment.PaymentInfo;
import com.atguigu.daijia.model.form.payment.PaymentInfoForm;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;
import com.atguigu.daijia.payment.config.WxPayV3Properties;
import com.atguigu.daijia.payment.mapper.PaymentInfoMapper;
import com.atguigu.daijia.payment.service.WxPayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/2/14 14:55
 */
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private RSAAutoCertificateConfig rsaAutoCertificateConfig;

    @Resource
    private WxPayV3Properties wxPayV3Properties;

    @Resource
    private RabbitService rabbitService;

    @Override
    @SneakyThrows
    public WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm) {

        // 1.添加支付记录到支付表里
        // 判断：如果表存在订单支付记录，不需要添加
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderNo,paymentInfoForm.getOrderNo());
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
        if (paymentInfo==null) {

            paymentInfo = new PaymentInfo();
            BeanUtils.copyProperties(paymentInfoForm,paymentInfo);
            paymentInfo.setPaymentStatus(0);
            paymentInfoMapper.insert(paymentInfo);
        }
        // 2.创建微信支付使用对象1
        JsapiServiceExtension service =
                new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();

        // 3.创建request对象，封装微信支付需要参数
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        // TODO 测试
        amount.setTotal(1);
        request.setAmount(amount);
        request.setAppid(wxPayV3Properties.getAppid());
        request.setMchid(wxPayV3Properties.getMerchantId());
        String description = paymentInfo.getContent();
        if(description.length() > 127) {
            description = description.substring(0, 127);
        }
        request.setDescription(description);

        request.setNotifyUrl(wxPayV3Properties.getNotifyUrl());

        request.setOutTradeNo(paymentInfo.getOrderNo());

        // 获取用户信息
        Payer payer = new Payer();
        payer.setOpenid(paymentInfoForm.getCustomerOpenId());
        request.setPayer(payer);

        // 是否指定分账,不指定不能分账

        SettleInfo settleInfo = new SettleInfo();
        settleInfo.setProfitSharing(true);
        request.setSettleInfo(settleInfo);
        // 4.调用微信支付使用对象里面方法实现微信支付调用
        PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);

        // 5.根据返回结果，封装到WxPrepayVo里面。
        WxPrepayVo wxPrepayVo = new WxPrepayVo();
        BeanUtils.copyProperties(response,wxPrepayVo);
        wxPrepayVo.setTimeStamp(response.getTimeStamp());
        return wxPrepayVo;
    }

    @Override
    public Boolean queryPayStatus(String orderNo) {
        return null;
    }

    @Override
    public void wxnotify(HttpServletRequest request) {

    }

    @Override
    public void handleOrder(String orderNo) {

    }
}
