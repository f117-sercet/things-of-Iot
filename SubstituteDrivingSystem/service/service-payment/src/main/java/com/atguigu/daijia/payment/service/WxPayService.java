package com.atguigu.daijia.payment.service;

import com.atguigu.daijia.model.form.payment.PaymentInfoForm;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/2/14 14:55
 */
public interface WxPayService {
    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    Boolean queryPayStatus(String orderNo);

    void wxnotify(HttpServletRequest request);

    //支付成功后续处理
    void handleOrder(String orderNo);
}
