package com.portfolio.demo.project.config;

import com.portfolio.demo.project.util.KakaoLoginApiUtil;
import com.portfolio.demo.project.util.KakaoProfileApiUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoApiConfig {

    @Bean
    public KakaoLoginApiUtil kakaoLoginApiUtil() {
        return new KakaoLoginApiUtil();
    }

    @Bean
    public KakaoProfileApiUtil kakaoProfileApiUtil() {
        return new KakaoProfileApiUtil();
    }
}
