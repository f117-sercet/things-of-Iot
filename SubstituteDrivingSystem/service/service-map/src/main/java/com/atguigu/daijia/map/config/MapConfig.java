package com.atguigu.daijia.map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @create: Created in 2024/12/11 9:23
 */
@Configuration
public class MapConfig {

    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
