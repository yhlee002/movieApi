package com.portfolio.demo.project.service;

import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.MemberVO;
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

    MemberVO createUser() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.user().identifier("xxxoxxo00201@gmail.com").build()
                )
        );
    }

    @Test
    void 메일_발송() {
        mailService.send("xxxoxxo00201@gmail.com", "test mail", "test content.");
    }

    @Test
    void 인증_메일_발송() {
        // given
        MemberVO user = createUser();

        // when
        mailService.sendCertMail(user.getIdentifier());
    }

    @Test
    void 가입_축하_메일() {
        // given
        MemberVO user = createUser();

        // when
        mailService.sendGreetingMail(user.getIdentifier());
    }
}
