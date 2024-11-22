package com.daijia.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/22 10:14
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceDispatchApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceDispatchApplication.class);
    }
    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }


}
