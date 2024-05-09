package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MemberVO {
    private Long memNo;
    private String identifier;
    private String name;
    private String profileImage; // 프로필조회 api사용시 api에서 제공하는 profile_image를 담아 객체째로 세션에 담을 것.
    private String phone;
    private String regDate;
    private String role;
    private String provider;

    public static MemberVO create(Member member) {
        return MemberVO.builder()
                .memNo(member.getMemNo())
                .identifier(member.getIdentifier())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .phone(member.getPhone())
                .regDate(member.getRegDate())
                .role(member.getRole())
                .provider(member.getProvider())
                .build();
    }
}
