package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.certification.CertificationReason;
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
public class CertificationServiceTest {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private MemberService memberService;

    private ResourceBundle resource = ResourceBundle.getBundle("Res_ko_KR_keys");
    private String samplePhoneNumber = resource.getString("aws.sample.phone");
    private String sampleEmailAddress = resource.getString("aws.sample.email");

    MemberParam createUser() {
        Long id = memberService.saveMember(
                MemberParam.create(
                        MemberTestDataBuilder.user().identifier("").build()
                )
        );
        return memberService.findByMemNo(id);
    }

    @Test
    void 메일_발송() {
        certificationService.sendEmail(sampleEmailAddress, "test mail", "test content.");
    }

    @Test
    void 인증_메일_발송() {
        // given
        MemberParam user = createUser();

        // when
        certificationService.sendCertificationMail(user.getIdentifier(), CertificationReason.SIGNUP);
    }

    @Test
    void 인증_문자_발송() {
        //given
        certificationService.sendCertificationMessage(samplePhoneNumber, CertificationReason.FINDPASSWORD);
    }
}
