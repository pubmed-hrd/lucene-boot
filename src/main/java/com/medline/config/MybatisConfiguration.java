package com.medline.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.medline.repository")
public class MybatisConfiguration {

}
