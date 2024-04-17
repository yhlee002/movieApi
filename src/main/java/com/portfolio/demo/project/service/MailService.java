package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    private final MemberRepository memberRepository;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String fromMail = resourceBundle.getString("mail");

    protected Map<String, String> send(String toMail, String title, String content) {
        Map<String, String> result = new HashMap<>();
        MimeMessage message = mailSender.createMimeMessage();
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props);
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setFrom(fromMail);
            messageHelper.setTo(toMail);
            messageHelper.setSubject(title);
            messageHelper.setText(content, true); //html형식으로 전송

            mailSender.send(message);

            result.put("resultCode", "success");
            return result;
        } catch (MessagingException e) {
            e.printStackTrace();

            result.put("resultCode", "fail");
            return result;
        }
    }

    public Map<String, String> sendCertMail(String email) {
        log.info("들어온 메일 주소 : " + email);
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "SiteName 비밀번호 찾기 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://3.36.203.4:8080/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 맞으시면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://3.36.203.4:8080/findPwd/certificationEmail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";

        Map<String, String> result = send(tomail, title, content);

        if (result.get("resultCode").equals("success")) {
            saveCertKey(member, certKey);
        } else{
            log.info("메일 전송 실패");
        }

        return result;

    }

    public Map<String, String> sendGreetingMail(String email) {
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "SiteName 회원가입 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://3.36.203.4:8080/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 가입하신것이 맞다면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://3.36.203.4:8080/sign-up/certificationEmail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";


        Map<String, String> result = send(tomail, title, content);
        if (result.get("resultCode").equals("success")) {
            saveCertKey(member, certKey);
        } else{
            log.info("메일 전송 실패");
        }

        return result;
    }

    // 인증키 저장(갱신)
    protected void saveCertKey(Member member, String certKey) {
        memberRepository.save(Member.builder()
                .memNo(member.getMemNo())
                .identifier(member.getIdentifier())
                .name(member.getName())
                .password(member.getPassword())
                .phone(member.getPhone())
                .role(member.getRole())
                .regDt(member.getRegDt())
                .provider("none")
                .certKey(passwordEncoder.encode(certKey))
                .certification(member.getCertification())
                .build()
        );
    }
}

