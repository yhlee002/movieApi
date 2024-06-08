//package com.portfolio.demo.project.config;
//
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//
//    // Lettuce 사용시
//    @Bean
//    LettuceConnectionFactory lettuceConnectionFactory() {
//        return new LettuceConnectionFactory();
//    }
//
//    // Jedis 사용시
//    /*
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName("127.0.0.1");
//        config.setPort(6379);
//
//        return new JedisConnectionFactory(config);
//    }
//     */
//
//    @Bean
//    RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(lettuceConnectionFactory()); // 또는 jedisConnectionFactory()
//
//        return template;
//    }
//}
