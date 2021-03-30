package com.lming.ltts.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.lming.ltts.gateway.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 1 * @Author: zhangliangming
 * 2 * @Date: 2021/3/30 0030 上午 9:22
 * 3 * @Descripiton:  ip拦截
 */
@Slf4j
@Component
@RefreshScope
public class IpFilter implements GlobalFilter {


    @Value("${blackIpList}")
    private List<String> blackIpList ;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("blackIpList:{}",blackIpList);

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String ipAddress = getIpAddress(headers, request);
        log.info("==> 请求ip地址:{}",ipAddress);
        if(isBlackUrl(ipAddress)){

            log.warn("==> 限制访问列表：{},ip:{}",blackIpList,ipAddress);
            ServerHttpResponse response = exchange.getResponse();

            return exchange.getResponse().writeWith(
                    Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(R.fail("IP限制访问")))));
        }

        // 往下传递
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
        }));
    }



    private boolean isBlackUrl(String ipAddress){

        if(CollectionUtils.isEmpty(blackIpList)){
            return false;
        }

        for(String blackIp : blackIpList){

            if(blackIp.equals(ipAddress)){
                return true;
            }

            Pattern pattern = Pattern.compile(blackIp.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE);
            if(pattern.matcher(ipAddress).find()){
                return true;
            }
        }

        return false;
    }


    private String getIpAddress(HttpHeaders headers, ServerHttpRequest request){
        String ip = headers.getFirst("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                    ip = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    log.info("读取本机IP地址失败。");
                }
            }
        }
        //对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() =15
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
