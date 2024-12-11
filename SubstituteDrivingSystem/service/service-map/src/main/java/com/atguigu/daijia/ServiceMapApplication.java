package com.atguigu.daijia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @create: Created in 2024/12/11 9:16
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class )
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMapApplication.class,args);
    }
    @Bean
    public RestTemplate restemplate(){
        return new RestTemplate();
    }
}
