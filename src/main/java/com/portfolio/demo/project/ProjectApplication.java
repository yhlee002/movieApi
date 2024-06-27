package com.portfolio.demo.project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.portfolio.demo.project.repository"})
@EnableJpaAuditing
@EntityScan("com.portfolio.demo.project.entity")
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
        CustomHost customHost = new CustomHost();
    }

    @Component
    public static class CustomHost implements CommandLineRunner {

        @Value("${web.server.host}")
        private String host;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("###### 현재 API 서버 경로: " + host + " ######");
        }
    }
}
