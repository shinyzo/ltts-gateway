package com.lming.ltts.gateway.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * Created by chenws on 2019/11/1.
 * 鉴权过滤器
 */
@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Conifg> {

    @Override
    public GatewayFilter apply(AuthenticationGatewayFilterFactory.Conifg config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            URI uri = request.getURI();
            String path = uri.getPath();
            log.info("鉴权过滤器，拦截的uri [{}]", path);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            }));
        };
    }




    public static class Conifg{
        private List<String> authUrls;

        public List<String> getAuthUrls() {
            return authUrls;
        }

        public void setAuthUrls(List<String> authUrls) {
            this.authUrls = authUrls;
        }
    }

}
