package com.portfolio.demo.project.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialLoginParam {
    private String provider; // naver, kakao
    private String state;
    private String apiUrl; // login url
}
