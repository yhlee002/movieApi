package com.portfolio.demo.project.dto.social;

import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialLoginParam {
    private SocialLoginProvider provider; // naver, kakao
    private String state;
    private String apiUrl; // login url
}
