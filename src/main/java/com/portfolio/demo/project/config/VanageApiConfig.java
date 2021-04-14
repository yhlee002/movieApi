package com.portfolio.demo.project.config;

import com.portfolio.demo.project.util.VonageMessageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VanageApiConfig {
    @Bean
    VonageMessageUtil vonageMessageUtil() {
        return new VonageMessageUtil();
    }
}
