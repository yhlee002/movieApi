package com.portfolio.demo.project.config;

import com.portfolio.demo.project.util.SeleniumUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {
    @Bean
    public SeleniumUtil seleniumUtil() {
        return new SeleniumUtil();
    }
}
