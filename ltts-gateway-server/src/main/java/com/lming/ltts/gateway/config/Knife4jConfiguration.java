package com.lming.ltts.gateway.config;

import com.alibaba.fastjson.JSONArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 1 * @Author: zhangliangming
 * 2 * @Date: 2021/4/1 0001 下午 5:23
 * 3 * @Descripiton:
 */
@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class Knife4jConfiguration implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;

    private final GatewayProperties gatewayProperties;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 遍历网关的路由，并通过restTemplate访问后端服务的swagger信息，聚合后返回
     *
     * @return
     */
    @Override
    public List<SwaggerResource> get() {
        String url = "/swagger-resources?lamp=";
        //获取所有router
        List<SwaggerResource> resources = new ArrayList<>();

        List<String> routes = new ArrayList<>();
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        gatewayProperties.getRoutes().stream()
                .filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(route -> {
                    route.getPredicates().stream()
                            .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                            .forEach(predicateDefinition -> {
                                        try {
                                            // knife4j 官方提供的demo中，只能聚合group=default的文档，我这里做了增强，能聚合所有group
                                            JSONArray list = restTemplate.getForObject("http://" + route.getUri().getHost() + url, JSONArray.class);
                                            if (!list.isEmpty()) {
                                                for (int i = 0; i < list.size(); i++) {
                                                    SwaggerResource sr = list.getObject(i, SwaggerResource.class);
                                                    resources.add(swaggerResource(route.getId() + "-" + sr.getName(), "/" + route.getId() + sr.getUrl()));
                                                }
                                            }
                                        } catch (Exception e) {
                                            log.warn("加载后端资源时失败{}", route.getUri().getHost(), e);
                                        }
                                    }

                            );
                });

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        log.info("name:{},location:{}", name, location);
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

//    @Bean(value = "defaultApi2")
//    public Docket defaultApi2() {
//        Docket docket=new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(new ApiInfoBuilder()
//                        //.title("swagger-bootstrap-ui-demo RESTful APIs")
//                        .description("# swagger-bootstrap-ui-demo RESTful APIs")
//                        .termsOfServiceUrl("http://www.xx.com/")
//                        .contact("xx@qq.com")
//                        .version("1.0")
//                        .build())
//                //分组名称
//                .groupName("2.X版本")
//                .select()
//                //这里指定Controller扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.lming.ltts.gateway.controller"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
}
