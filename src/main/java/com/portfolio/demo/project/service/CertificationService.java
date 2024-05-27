package com.portfolio.demo.project.service;

import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import com.portfolio.demo.project.controller.member.certkey.CertificationType;
import com.portfolio.demo.project.entity.CertificationData;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CertificationRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.service.certification.SendCertificationNotifyResult;
import com.portfolio.demo.project.util.AwsSmsUtil;
import com.portfolio.demo.project.util.TempKey;
import dev.akkinoc.util.YamlResourceBundle;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationService {

    private final Environment environment;

    private final CertificationRepository certificationRepository;

    private final MemberRepository memberRepository;

    private final TempKey tempKey;

    private static ResourceBundle properties = ResourceBundle.getBundle("application", YamlResourceBundle.Control.INSTANCE);

    private String host = null;
    private Integer port = null;

    {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            port = (Integer) properties.getObject("server.port");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 식별번호를 이용한 인증정보 단건 조회
     *
     * @param id
     * @return
     */
    @Cacheable(value = "Certification")
    public CertificationDataDto findById(Long id) {
        CertificationData data = certificationRepository.findById(id).orElse(null);
        return new CertificationDataDto(data);
    }

    /**
     * 전화번호 또는 이메일을 이용한 인증정보 단건 조회
     *
     * @param certificationId
     * @return
     */
    @Cacheable(value = "Certification")
    public CertificationDataDto findByCertificationIdAndType(String certificationId, CertificationType type) {
        CertificationData data = certificationRepository.findByCertificationIdAndType(certificationId, type);

        return new CertificationDataDto(data);
    }

    /**
     * 인증정보 생성
     *
     * @param param
     */
    public Long saveCertification(CertificationDataDto param) {
        CertificationData data = CertificationData.builder()
                .certificationId(param.getCertificationId())
                .type(param.getCertificationType())
                .certKey(param.getCertKey())
                .expiration(param.getExpiration())
                .build();
        certificationRepository.save(data);

        return data.getId();
    }

    /**
     * 인증정보 수정
     *
     * @param param
     */
    public void updateCertification(CertificationDataDto param) {
        CertificationData data = certificationRepository.findByCertificationId(param.getCertificationId());

        if (data != null) {
            data.setCertKey(param.getCertKey());
            data.setExpiration(LocalDateTime.now());
        } else {
            throw new IllegalStateException("요청에 일치하는 인증정보가 존재하지 않습니다.");
        }
    }

    /**
     * 인증정보 삭제
     *
     * @param param
     */
    public void deleteCertification(CertificationDataDto param) {
        CertificationData data = certificationRepository.findByCertificationIdAndType(param.getCertificationId(), param.getCertificationType());
        certificationRepository.delete(data);
    }

    /**
     * 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송(결과 반환) + 인증키 서버로 다시 보내기
     *
     * @param phone
     * @return
     */
    public SendCertificationNotifyResult sendCertificationMessage(String phone) {
        String certKey = Integer.toString(getTempKey());
//        String result = messageUtil.sendPinNumber(certKey, phone);
        Boolean success = AwsSmsUtil.sendMessage(certKey, phone);

        if (success) {
            CertificationData data = CertificationData.builder()
                    .certificationId(phone)
                    .type(CertificationType.PHONE)
                    .certKey(certKey)
                    .expiration(LocalDateTime.now())
                    .build();

            certificationRepository.save(data);
            CertificationDataDto dto = new CertificationDataDto(data);

            return new SendCertificationNotifyResult(Boolean.TRUE, dto);
        }

        return new SendCertificationNotifyResult(Boolean.FALSE, null);
    }

    public Boolean sendCertMail(String email) {
        log.info("들어온 메일 주소 : " + email);
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "MovieSite 비밀번호 찾기 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://" + host + ":" + port + "/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 맞으시면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://" + host + ":" + port + "/findPwd/cert-mail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";

        return sendEmail(tomail, title, content);
    }

    public Boolean sendGreetingMail(String email) {
        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(email);

        String tomail = email;
        String title = "MovieSite 회원가입 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://" + host + ":" + port + "/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 가입하신것이 맞다면 다음 링크를 눌러주세요.</p>"
                + "인증하기 링크 : <a href='http://" + host + ":" + port + "/sign-up/cert-mail?memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";

        return sendEmail(tomail, title, content);
    }

    protected Boolean sendEmail(String toMail, String title, String content) {
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

            return Boolean.TRUE;
        } catch (MessagingException e) {
            e.printStackTrace();
            Exception ex = null;
            if ((ex = e.getNextException()) != null) {
                ex.printStackTrace();
            }

            log.info("메일 전송에 실패하였습니다. (이메일 주소: {})", toMail);

            return Boolean.FALSE;
        }
    }

    // 랜덤 키 생성
    private int getTempKey() {
        Random ran = new Random();
        return ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성
    }
}
