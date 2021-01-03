package com.portfolio.demo.project.util;

import com.portfolio.demo.project.service.BoxOfficeService;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ScrapingMovieImgs {

    @Autowired
    private static BoxOfficeService boxOfficeService;

    @PostConstruct
    public void initialize() {
        System.out.println("encode : " + URLEncoder.encode("%uC6D0%uB354%uC6B0%uBA3C%201984"));
        System.out.println("decode : " + URLDecoder.decode("%uC6D0%uB354%uC6B0%uBA3C%201984"));
    }

    @Synchronized
    public static void main(String[] args) {
        boxOfficeService.getDailyBoxOfficeList();
    }
}
