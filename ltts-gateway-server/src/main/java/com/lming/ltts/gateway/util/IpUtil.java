package com.lming.ltts.gateway.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 1 * @Author: zhangliangming
 * 2 * @Date: 2021/3/30 0030 下午 5:09
 * 3 * @Descripiton:
 */
public class IpUtil {


    public static boolean isMatch(String regexIp,String ipAddress){
        Pattern pattern = Pattern.compile(regexIp.replaceAll("\\*", "(.*?)"), Pattern.CASE_INSENSITIVE);
        if(pattern.matcher(ipAddress).matches()){
            return true;
        }
        return false;
    }


    public static boolean isMatch(List<String> ipList, String ipAddress){

        if(CollectionUtils.isEmpty(ipList)){
            return false;
        }

        for(String regexIp : ipList){
            if(regexIp.equals(ipAddress)){
                return true;
            }
            return isMatch(regexIp,ipAddress);
        }

        return false;
    }



    public static String getIpAddress(HttpHeaders headers, ServerHttpRequest request){
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
            if ("127.0.0.1".equals(ip)) {
                // 根据网卡取本机配置的IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                    ip = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
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

    public static void main(String[] args) {
        System.out.println(isMatch("*.*.22.99","192.168.22.99"));
    }
}
