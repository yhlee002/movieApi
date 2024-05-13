package com.portfolio.demo.project.config;

import com.portfolio.demo.project.util.MovieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovieConfig {

    @Bean
    public MovieUtil movieUtil() {
        return new MovieUtil();
    }
}
