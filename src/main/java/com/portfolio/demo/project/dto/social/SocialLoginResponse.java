package com.portfolio.demo.project.dto.social;

import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocialLoginResponse {
    @Enumerated(EnumType.STRING)
    private SocialLoginProvider provider;
    private Boolean result;
    private String identifier; // 사용자가 제공한 identifier
    @Enumerated(EnumType.STRING)
    private SocialLoginStatus status;
    private String message;
}
