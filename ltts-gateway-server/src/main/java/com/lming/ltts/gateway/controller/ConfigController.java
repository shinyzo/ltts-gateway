package com.lming.ltts.gateway.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 1 * @Author: zhangliangming
 * 2 * @Date: 2021/3/30 0030 下午 2:17
 * 3 * @Descripiton:
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${name:11111}")
    private String name;

    @RequestMapping("/get")
    @SentinelResource("config")
    public String get() {
        return name;
    }
}
