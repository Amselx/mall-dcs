package com.luban.search.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan({"com.luban.mapper","com.luban.search.dao"})
public class MyBatisConfig {
}
