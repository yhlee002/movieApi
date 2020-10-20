//package com.portfolio.demo.project.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
//import org.springframework.web.servlet.resource.VersionResourceResolver;
//import org.springframework.web.servlet.resource.WebJarsResourceResolver;
//import sun.misc.Version;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Bean
//    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
//        return new ResourceUrlEncodingFilter();
//    }
//
////    @Override
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////        registry.addResourceHandler("/webjars/**")
////                .addResourceLocations("classpath:/webjars/**")
////                .setCachePeriod(60 * 60 * 24 * 365)
////                .resourceChain(true).addResolver(new WebJarsResourceResolver())
////                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
////        //모든 경로의 웹 리소스 파일들을 로드 할시 파일명을 해싱해줌
////
////    }
//}
