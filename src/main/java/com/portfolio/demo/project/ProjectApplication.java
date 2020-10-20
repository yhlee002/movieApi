package com.portfolio.demo.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.portfolio.demo.project.repository"})
//@ComponentScan({
//        "com.portfolio.demo.project.config",
//        "com.portfolio.demo.project.controller",
//        "com.portfolio.demo.project.service"
//})
//@ComponentScan(basePackages = "com.portfolio.demo.project")
@EntityScan("com.portfolio.demo.project.entity")
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

}
