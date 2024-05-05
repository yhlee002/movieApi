package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

//    private final JavaMailSender mailSender;

    private final Environment environment;

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    private final MemberRepository memberRepository;

    private static ResourceBundle properties = ResourceBundle.getBundle("application");

    private String host = null;
    private Integer port = null;

    {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            port = Integer.parseInt(properties.getString("server.port"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, String> send(String toMail, String title, String content) {

        Map<String, String> result = new HashMap<>();

        try {
            String username = environment.getProperty("spring.mail.username");
            String password = environment.getProperty("spring.mail.password");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };

            Session session = Session.getInstance(props, authenticator);
            session.setDebug(true);

            Message msg = new MimeMessage(session);

            Address toAddr = new InternetAddress(toMail);
            Address fromAddr = new InternetAddress(username);

            msg.setFrom(fromAddr);
            msg.setRecipient(Message.RecipientType.TO, toAddr);
            msg.setSubject(title);
            msg.setContent(content, "text/html; charset=utf-8");
            msg.setSentDate(new Date());

            Transport.send(msg);

            result.put("resultCode", "success");
            return result;
        } catch (MessagingException e) {
            e.printStackTrace();
            Exception ex = null;
            if ((ex = e.getNextException()) != null) {
                ex.printStackTrace();
            }
            result.put("resultCode", "fail");
            return result;
        }
    }

    public Map<String, String> sendCertMail(String email) {
        log.info("들어온 메일 주소 : " + email);
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "MovieSite 비밀번호 찾기 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://" + host + ":" + port + "/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 맞으시면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://" + host + ":" + port + "/findPwd/certificationEmail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";

        Map<String, String> result = send(tomail, title, content);

        if (result.get("resultCode").equals("success")) {
            saveCertKey(member, certKey);
        } else {
            log.info("메일 전송 실패");
        }

        return result;

    }

    public Map<String, String> sendGreetingMail(String email) {
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "MovieSite 회원가입 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://" + host + ":" + port + "/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 가입하신것이 맞다면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://" + host + ":" + port + "/sign-up/certificationEmail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";


        Map<String, String> result = send(tomail, title, content);
        if (result.get("resultCode").equals("success")) {
            saveCertKey(member, certKey);
        } else {
            log.info("메일 전송 실패");
        }

        return result;
    }

    // 인증키 저장(갱신)
    protected void saveCertKey(Member member, String certKey) {
        member.updateProvider("none");
        member.updateCertKey(passwordEncoder.encode(certKey));
        memberRepository.save(member);
    }
}

