package com.daijia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/5 10:02
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceCustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCustomerApplication.class, args);
    }

}
