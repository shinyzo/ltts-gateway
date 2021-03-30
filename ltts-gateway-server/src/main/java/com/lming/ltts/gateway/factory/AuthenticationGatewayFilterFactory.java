package com.lming.ltts.gateway.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenws on 2019/11/1.
 * 鉴权过滤器
 */
@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {


    public static final String AUTH_URL_KEY = "authUrls";


    public AuthenticationGatewayFilterFactory() {
        super(AuthenticationGatewayFilterFactory.Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(new String[]{AUTH_URL_KEY});
    }

    @Override
    public GatewayFilter apply(final AuthenticationGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            URI uri = request.getURI();
            String path = uri.getPath();
            log.info("鉴权过滤器，拦截的uri [{}]", path);

            if(isAuth(path)){

            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            }));
        };
    }


    private boolean isAuth(String path){
        return true;
    }


    public static class Config{
        private List<String> authUrls;

        public Config() {
        }
        public List<String> getAuthUrls() {
            return authUrls;
        }

        public void setAuthUrls(List<String> authUrls) {
            this.authUrls = authUrls;
        }
    }

}
