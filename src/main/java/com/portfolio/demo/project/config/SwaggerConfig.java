package com.portfolio.demo.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// OpenAPI 명세서의 기본 정보를 정의
@OpenAPIDefinition(info = @Info(
        title = "Movie Site API 명세서",
        version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi v1Api() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Movie Site API v1") // 그룹 이름 정의
                .pathsToMatch(paths) // 그룹에 포함할 경로 패턴 정의
                .build();
    }
}
