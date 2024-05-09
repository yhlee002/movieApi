package com.portfolio.demo.project.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialProfile {
    private String id;
    private String nickname;
    private String profileImageUrl; // profile_image_url
}
