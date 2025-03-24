package com.atguigu.daijia.rules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2025/2/19 10:17
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
// 项目启动报错修改
public class ServiceRulesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRulesApplication.class,args);
    }
}
