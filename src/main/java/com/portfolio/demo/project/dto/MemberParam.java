package com.portfolio.demo.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@ToString
public class MemberParam {
    private Long memNo;
    private String identifier;
    @JsonIgnore
    private String password;
    private String name;
    private String profileImage;
    private String phone;
    private LocalDateTime regDate;
    private String role;
    private String provider;
    private String certification;

    public static MemberParam create(Member member) {
        return MemberParam.builder()
                .memNo(member.getMemNo())
                .identifier(member.getIdentifier())
                .password(member.getPassword())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .phone(member.getPhone())
                .regDate(member.getRegDate())
                .role(member.getRole())
                .provider(member.getProvider())
                .certification(member.getCertification())
                .build();
    }
}
