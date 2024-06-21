package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.member.MemberParam;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ResourceBundle;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PhoneMessageServiceTest {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private MemberService memberService;

    private final ResourceBundle resource = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final String samplePhoneNumber = resource.getString("aws.sample.phone");

    /* 빌드 과정중의 테스트시 불필요한 sms API 호출 방지를 위해 주석 처리
    @Test
    void sendCertificationMessage() {
        // given
        Member member = MemberTestDataBuilder.user().name("devYH").phone(samplePhoneNumber).build();
        memberService.saveMember(MemberParam.create(member));

        // when
        certificationService.sendCertificationMessage(samplePhoneNumber, CertificationReason.SIGNUP);
    }
     */
}