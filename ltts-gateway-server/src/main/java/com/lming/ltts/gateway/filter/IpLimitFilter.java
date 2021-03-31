package com.lming.ltts.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.lming.ltts.gateway.common.R;
import com.lming.ltts.gateway.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 1 * @Author: zhangliangming
 * 2 * @Date: 2021/3/30 0030 上午 9:22
 * 3 * @Descripiton:  ip限制访问过滤器
 */
@Slf4j
@Component
@RefreshScope
public class IpLimitFilter implements GlobalFilter {


    @Value("${blackIpList}")
    private List<String> blackIpList ;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String ipAddress = IpUtil.getIpAddress(headers, request);
        log.info("==> 请求ip地址:{}",ipAddress);

        if(IpUtil.isMatch(blackIpList,ipAddress)){
            log.warn("==> 限制访问列表：{},ipAddress:{}",blackIpList,ipAddress);
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return exchange.getResponse().writeWith(Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(R.fail("非法IP,无法访问服务")))));
        }

        // 往下传递
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
        }));
    }

}
