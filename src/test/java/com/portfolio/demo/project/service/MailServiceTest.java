package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MailServiceTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private MemberService memberService;

    @Test
    void 메일_발송() {
        mailService.send("xxxoxxo002@gmail.com", "test mail", "test content.");
    }

    @Test
    void 인증_메일_발송() {
        // given
        Member user = MemberTestDataBuilder.user().identifier("xxxoxxo002@gmail.com").build();
        memberService.saveMember(user);

        // when
        mailService.sendCertMail(user.getIdentifier());
    }

    @Test
    void 가입_축하_메일() {
        // given
        Member user = MemberTestDataBuilder.user().identifier("xxxoxxo002@gmail.com").build();
        memberService.saveMember(user);

        // when
        mailService.sendGreetingMail(user.getIdentifier());
    }
}
