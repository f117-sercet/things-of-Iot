package com.daijia.common.config.mybatisPlus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/3 8:49
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.daijia.*.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor optimisicLockerInnerInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 向Mybatis过滤器链种添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return  interceptor;
    }
}
