package com.portfolio.demo.project.config;

import com.portfolio.demo.project.util.BoxOfficeListUtil;
import com.portfolio.demo.project.util.DailyBoxOfficeListUtil;
import com.portfolio.demo.project.util.MovieInfoUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoxOfficeConfig {
    @Bean
    public DailyBoxOfficeListUtil dailyBoxOfficeListUtil() {
        return new DailyBoxOfficeListUtil();
    }

    @Bean
    public MovieInfoUtil movieInfoUtil() {
        return new MovieInfoUtil();
    }

    @Bean
    public BoxOfficeListUtil boxOfficeListUtil() {
        return new BoxOfficeListUtil();
    }
}
