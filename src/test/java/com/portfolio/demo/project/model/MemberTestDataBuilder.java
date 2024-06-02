package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;

import java.time.LocalDateTime;
import java.util.Random;

public class MemberTestDataBuilder {

    private static final Random random = new Random();

    public static Member.MemberBuilder admin() {
        return Member.builder()
                .identifier("dldudgus214@naver.com")
                .password("1234")
                .name("yhlee")
                .phone("000-000-0000")
                .role(MemberRole.ROLE_ADMIN)
                .provider("none")
                .certification(MemberCertificated.Y);
    }

    public static Member.MemberBuilder user() {
        return Member.builder()
                .identifier("xxxoxxo002@gmail.com")
                .password("1234")
                .name("이영현")
                .phone("000-111-0000")
                .provider("none")
                .certification(MemberCertificated.Y);
    }

    public static Member.MemberBuilder noPasswordUser() {
        return Member.builder()
                .identifier("xxxoxxo002@gmail.com")
                .password(null)
                .name("이영현")
                .phone("000-111-0000")
                .provider("none")
                .certification(MemberCertificated.N);
    }

    public static Member.MemberBuilder naverUser() {
        return Member.builder()
                .identifier("353896214578115")
                .password("")
                .name("네이버유저")
                .phone("000-1212-3434")
                .provider("naver")
                .certification(MemberCertificated.Y);
    }

    public static Member.MemberBuilder randomIdentifierUser() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append((char) random.nextInt(26));
        }
        String randomString = sb.toString();

        return Member.builder()
                .identifier("test" + new Random().nextInt(10000) + "@gmail.com")
                .password("1234")
                .name(randomString)
                .phone("000-" + random.nextInt(100, 999) + "-" + random.nextInt(1000, 9999))
                .provider("none")
                .certification(MemberCertificated.N);
    }
}
