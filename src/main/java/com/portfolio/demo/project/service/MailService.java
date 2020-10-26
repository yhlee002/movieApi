package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.CERT;
import com.portfolio.demo.project.util.TempKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TempKey tempKey;

    @Autowired
    MemberRepository memberRepository;

    public String sendMail(String email) {

        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByEmail(email);

        String setFrom = "testaccyh002@gmail.com";
        String tomail = email; // member.getEmail()
        String title = "SiteName 회원가입 인증 메일";
        String content = "<h2>안녕하세요." + member.getName() + "님</h2>"
                + "<p>본인이 가입하신것이 맞다면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://localhost:8080/sign-up/certificationEmail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>";
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setFrom(setFrom);
            messageHelper.setTo(tomail);
            messageHelper.setSubject(title);
            messageHelper.setText(content, true); //html형식으로 전송

            mailSender.send(message);

            memberRepository.save(Member.builder()
                    .memNo(member.getMemNo())
                    .email(member.getEmail())
                    .name(member.getName())
                    .password(member.getPassword())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .regDt(member.getRegDt())
                    .certKey(passwordEncoder.encode(certKey))
                    .certification(member.getCertification())
                    .build()
            );
            return "인증 메일 전송 성공";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "인증 메일 전송 실패";
        }
    }

//    public void sendEmailForPwd(Member member) {
//        //인증키 갱신
//        String authKey = tempKey.getKey(10, false);
//        userAuthMapper.updateUserWithKey(user.getId(), authKey);
//
//        String setFrom = "teambookiecs@gmail.com";
//        String tomail = user.getUEmail();
//        String title = "bookie 비밀번호 재설정 인증";
//        String content = "<h2></h2><p>비밀번호를 재설정 하시려면 다음 링크를 눌러주세요.</p>"
//                + "인증하기 링크 : <a href='http://localhost:8080/checkEmail?uId=" + user.getId() + "&authkey=" + authKey + "'>";
//
//        MimeMessage message = mailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//
//            messageHelper.setFrom(setFrom);
//            messageHelper.setTo(tomail);
//            messageHelper.setSubject(title);
//            messageHelper.setText(content, true);
//
//            mailSender.send(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
