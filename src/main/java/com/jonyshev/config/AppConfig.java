package com.jonyshev.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.jonyshev")
@Import({DatabaseConfig.class})
public class AppConfig {
}
