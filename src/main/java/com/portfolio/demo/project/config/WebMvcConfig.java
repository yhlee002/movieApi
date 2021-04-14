package com.portfolio.demo.project.config;

import com.portfolio.demo.project.intercepter.RememberMeIntercepter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //외부 경로 리소스를 url로 불러올 수 있게함

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/summernoteImage/**") // 써머노트 이미지 업로드
                .addResourceLocations("file:///home/ec2-user/app/git/web_movie/summernoteImageFiles/");
//                .addResourceLocations("file:///C:/Users/Admin/IdeaProjects/webProject/summernoteImageFiles/");
        registry.addResourceHandler("/profileImage/**") // 프로필 이미지 업로드
                .addResourceLocations("file:///home/ec2-user/app/git/web_movie/profileImages/");
//                .addResourceLocations("file:///C:/Users/Admin/IdeaProjects/webProject/profileImages/");

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
}
