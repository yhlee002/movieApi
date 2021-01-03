package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.member.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MemberVO {

    private String identifier;
    private String nickname;
    private String profileImage; // 프로필조회 api에서 제공하는 profile_image를 담아 객체째로 세션에 담을 것.
    private String phone;
    private LocalDateTime regDt;
    private String role;

    public MemberVO(Member member) {
        this.identifier = member.getIdentifier();
        this.nickname = member.getName();
        this.profileImage = member.getProfileImage();
        this.phone = member.getPhone();
        this.regDt = member.getRegDt();
        this.role = member.getRole();
    }
}
