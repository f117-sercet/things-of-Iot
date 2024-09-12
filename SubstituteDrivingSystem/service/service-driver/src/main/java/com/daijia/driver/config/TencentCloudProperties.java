package com.daijia.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/12 17:57
 */
@Data
@Component
@ConfigurationProperties(prefix ="tencent.cloud" )
public class TencentCloudProperties {

    private  String secretId;
    private  String secretKey;
    private String bucketPrivate;

    private String persionGroupId;
}
