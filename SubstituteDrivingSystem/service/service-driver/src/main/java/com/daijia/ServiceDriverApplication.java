package com.daijia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/11 18:26
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceDriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDriverApplication.class, args);
    }
}
