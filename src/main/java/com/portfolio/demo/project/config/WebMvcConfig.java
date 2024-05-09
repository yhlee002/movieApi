package com.portfolio.demo.project.config;

import com.portfolio.demo.project.intercepter.RememberMeIntercepter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
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

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(formatter); // formatter
//        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter); // formatter
//
//        JavaTimeModule module = new JavaTimeModule();
//        module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
//        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(module);
//        // add converter at the very front
//        // if there are same type mappers in converters, setting in first mapper is used.
//        converters.add(0, new MappingJackson2HttpMessageConverter(mapper));
//    }

//    @Bean
//    public ObjectMapper getCustomObjectMapper() {
//        final ObjectMapper mapper = new ObjectMapper();
//        final SimpleModule module = new SimpleModule();
//        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
//        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
//        mapper.registerModule(module);
//        return mapper;
//    }

}
