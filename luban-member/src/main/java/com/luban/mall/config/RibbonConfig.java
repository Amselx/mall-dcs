package com.luban.mall.config;

import com.luban.mall.Component.LubanRestTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RibbonConfig {

    @Bean
    public LubanRestTemplate restTemplate(DiscoveryClient discoveryClient) {
        return new LubanRestTemplate(discoveryClient);
    }
}
