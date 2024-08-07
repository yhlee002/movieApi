package com.portfolio.demo.project.config;

import com.portfolio.demo.project.intercepter.RememberMeIntercepter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //외부 경로 리소스를 url로 불러올 수 있게함

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/summernoteImage/**") // 써머노트 이미지 업로드
//                .addResourceLocations("file:///home/ec2-user/app/git/web_movie/summernoteImageFiles/");
//        registry.addResourceHandler("/profileImage/**") // 프로필 이미지 업로드
//                .addResourceLocations("file:///home/ec2-user/app/git/web_movie/profileImages/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rememberMeIntercepter())
                .excludePathPatterns("/assets/**", "/images/**");
    }

    @Bean
    public RememberMeIntercepter rememberMeIntercepter() {
        return new RememberMeIntercepter();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost", "http://localhost/", "http://3.38.19.101")
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Auth-Token")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true)
                .maxAge(7200L);
    }
}
