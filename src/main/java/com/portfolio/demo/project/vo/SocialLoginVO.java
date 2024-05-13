package com.portfolio.demo.project.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialLoginVO {
    private String provider; // naver, kakao
    private String state;
    private String apiUrl; // login url
}
