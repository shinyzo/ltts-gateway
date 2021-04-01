package com.lming.ltts.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 网关限流配置
 * 
 * @author ruoyi
 */
@Configuration
public class GatewayConfig
{
//    @Bean
//    @Order(-1)
//    public GlobalFilter sentinelGatewayFilter()
//    {
//        return new SentinelGatewayFilter();
//    }

}