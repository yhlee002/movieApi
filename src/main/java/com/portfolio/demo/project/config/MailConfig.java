package com.portfolio.demo.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@RequiredArgsConstructor
@Configuration
public class MailConfig {

    private final Environment environment;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(environment.getProperty("spring.mail.host"));
//        javaMailSender.setUsername(environment.getProperty("spring.mail.username"));
//        javaMailSender.setPassword(environment.getProperty("spring.mail.password"));
        javaMailSender.setPort(587);
        javaMailSender.setProtocol("smtp");
//        javaMailSender.setJavaMailProperties(getMailProperties());
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", environment.getProperty("spring.mail.protocol"));
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.trust", environment.getProperty("spring.mail.host"));
        properties.setProperty("mail.smtp.ssl.enable","true");
        return properties;
    }
}
