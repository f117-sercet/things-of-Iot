package com.daijia.driver.service.impl;

import com.daijia.driver.config.TencentCloudProperties;
import com.daijia.driver.service.CiService;
import com.daijia.model.vo.order.TextAuditingVo;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import jakarta.annotation.Resource;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/19 17:11
 */
public class CiServiceImpl implements CiService {

    @Resource
    private TencentCloudProperties tencentCloudProperties;

    @Override
    public Boolean imageAuditing(String path) {

        // 1.创建请求对象
        ImageAuditingRequest request = new ImageAuditingRequest();

        //2.添加请求参数
        // 2.1设置请求bucket
        request.setBucketName(tencentCloudProperties.getBucketPrivate());
        //2.2设置审核策略 不传则为默认策略（预设）
        //request.setBizType("");
        //2.3设置 bucket 中的图片位置
        request.setObjectKey(path);
        //3.调用接口，获取任务响应对象



        return null;
    }

    @Override
    public TextAuditingVo textAuditing(String content) {
        return null;
    }
}
