package com.daijia.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/11 11:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServerGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class);
    }
}
