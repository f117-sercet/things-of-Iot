package com.daijia.driver.service.impl;

import com.daijia.driver.config.TencentCloudProperties;
import com.daijia.driver.service.CiService;
import com.daijia.model.vo.order.TextAuditingVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ciModel.auditing.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.Base64;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;

import java.util.List;

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
        COSClient client = this.getCosClient();
        ImageAuditingResponse response = client.imageAuditing(request);
        client.shutdown();

        // 用于返回该审核场景的审核结果，返回值:0，正常:1 确认为当前场景的违规内容;2.疑似为当前场景的违规内容；
        if (!response.getPornInfo().getHitFlag().equals("0")
                || !response.getAdsInfo().getHitFlag().equals("0")
                || !response.getTerroristInfo().getHitFlag().equals("0")
                || !response.getPoliticsInfo().getHitFlag().equals("0")
        ) {
            return false;
        }
        return true;
    }

    @Override
    public TextAuditingVo textAuditing(String content) {

        if (!StringUtils.hasText(content)) {
            TextAuditingVo textAuditingVo = new TextAuditingVo();
            textAuditingVo.setResult("0");
            return textAuditingVo;
        }
        COSClient cosClient = this.getCosClient();

        // 创建任务请求对象
        TextAuditingRequest request = new TextAuditingRequest();

        //2.添加请求参数
        request.setBucketName(tencentCloudProperties.getBucketPrivate());
        //2.1.1 设置请求内容，文本内容的Base64编码
        byte[] encode = Base64.encode(content.getBytes());
        request.getInput().setContent(new String(encode));
        request.getConf().setDetectType("all");

        //3.调用接口,获取任务响应对象
        TextAuditingResponse response = cosClient.createAuditingTextJobs(request);

        AuditingJobsDetail detail = response.getJobsDetail();
        TextAuditingVo textAuditingVo = new TextAuditingVo();

        if ("Success".equals(detail.getState())) {
            //检测结果: 0（审核正常），1 （判定为违规敏感文件），2（疑似敏感，建议人工复核）
            String result = detail.getResult();

            // 违规关键词
            StringBuffer keywords = new StringBuffer();
            List<SectionInfo> sectionInfoList = detail.getSectionList();

        sectionInfoList.stream().forEach(sectionInfo -> {
            String pornInfoKeyWord = sectionInfo.getPornInfo().getKeywords();
            String illegalInfoKeyWord = sectionInfo.getIllegalInfo().getKeywords();
            String abuseInfoKeyWord = sectionInfo.getAbuseInfo().getKeywords();

            if (pornInfoKeyWord.length() > 0) {
                keywords.append(pornInfoKeyWord).append(",");
            }
            if (illegalInfoKeyWord.length() > 0) {
                keywords.append(illegalInfoKeyWord).append(",");
            }
            if (abuseInfoKeyWord.length() > 0) {
                keywords.append(abuseInfoKeyWord).append(",");
            }
        });
        textAuditingVo.setResult(result);
        textAuditingVo.setKeywords(keywords.toString());
        }

        return textAuditingVo;
    }

    private COSClient getCosClient() {
        String secretId = tencentCloudProperties.getSecretId();
        String secretKey = tencentCloudProperties.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 2.设置bucket的地域，COS地域
        Region region = new Region(tencentCloudProperties.getRegion());

        ClientConfig clientConfig = new ClientConfig(region);

        // 设置使用 https协议
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 3. 生成Cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);
        return  cosClient;

    }
}
