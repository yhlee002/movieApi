package com.portfolio.demo.project.dto.social;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialProfileParam {
    private String id;
    private SocialLoginProvider provider;
    private String name;
    private String phone;
    private String email;
//    private Boolean isEmailVerified;
//    private Boolean isEmailValid;
    private String thumbnailImageUrl;
    private String profileImageUrl;
}
