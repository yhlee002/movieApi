package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.member.Member;

import java.time.LocalDateTime;
import java.util.Random;

public class MemberTestDataBuilder {

    public static Member.MemberBuilder admin() {
        return Member.builder()
                .identifier("dldudgus214@naver.com")
                .name("이영현")
                .phone("000-000-0000")
                .role("role_admin")
                .provider("none");
    }

    public static Member.MemberBuilder user() {
        return Member.builder()
                .identifier("xxxoxxo002@gmail.com")
                .name("이영현")
                .phone("000-111-0000")
                .role("role_user")
                .provider("none");
    }

    public static Member.MemberBuilder randomIdentifierUser() {
        return Member.builder()
                .identifier("test" + new Random().nextInt(10000) + "@gmail.com")
                .role("role_user")
                .provider("none");
    }
}
