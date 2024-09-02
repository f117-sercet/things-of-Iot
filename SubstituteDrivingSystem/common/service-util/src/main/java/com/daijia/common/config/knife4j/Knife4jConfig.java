package com.daijia.common.config.knife4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/2 16:38
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi webApi() {

        return GroupedOpenApi.builder()
                .group("web-api")
                .pathsToMatch("/**")
                .build();

    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("代驾API接口文档")
                        .version("1.0")
                        .description("代驾API接口文档")
                        .contact(new Contact().name("段世超")));
    }
}
