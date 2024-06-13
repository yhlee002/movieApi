package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.certification.CertificationDataDto;
import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.certification.CertificationType;
import com.portfolio.demo.project.entity.certification.CertificationData;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CertificationRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.dto.certification.SendCertificationNotifyResult;
import com.portfolio.demo.project.util.CoolSmsMessageUtil;
import com.portfolio.demo.project.util.TempKey;
import dev.akkinoc.util.YamlResourceBundle;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final String mailUserName = properties.getString("spring.mail.username");
    private final String mailPassword = properties.getString("spring.mail.password");

    private String host;
    private Integer port;

    /**
     * 식별번호를 이용한 인증정보 단건 조회
     *
     * @param id 인증정보 식별번호
     */
    public CertificationDataDto findById(Long id) {
        CertificationData data = certificationRepository.findById(id).orElse(null);
        return data == null ? null : new CertificationDataDto(data);
    }

    /**
     * 전화번호 또는 이메일을 이용한 인증정보 단건 조회
     *
     * @param certificationId 인증에 사용된 전화번호 혹은 이메일
     * @param type            인증에 사용된 아이디(certificationId) 타입(전화번호 혹은 이메일)
     */
    public CertificationDataDto findByCertificationIdAndType(String certificationId, CertificationType type) {
        CertificationData data = certificationRepository.findByCertificationIdAndType(certificationId, type);

        return data == null ? null : new CertificationDataDto(data);
    }

    /**
     * 인증정보 생성
     *
     * @param param 인증정보
     */
    public Long saveCertification(CertificationDataDto param) {
        CertificationData data = CertificationData.builder()
                .certificationId(param.getCertificationId())
                .type(param.getCertificationType())
                .certKey(param.getCertKey())
                .reason(param.getReason())
                .expiration(param.getExpiration())
                .build();
        certificationRepository.save(data);

        return data.getId();
    }

    /**
     * 인증정보 수정
     *
     * @param param 인증정보
     */
    public void updateCertification(CertificationDataDto param) {
        CertificationData data = certificationRepository.findByCertificationId(param.getCertificationId());

        if (data != null) {
            data.setCertKey(param.getCertKey());
            data.setExpiration(LocalDateTime.now().plusMinutes(3));
        } else {
            throw new IllegalStateException("요청에 일치하는 인증정보가 존재하지 않습니다.");
        }
    }

    /**
     * 인증정보 삭제
     *
     * @param param 인증정보
     */
    public void deleteCertification(CertificationDataDto param) {
        CertificationData data = certificationRepository.findByCertificationIdAndType(param.getCertificationId(), param.getCertificationType());
        certificationRepository.delete(data);
    }

    /**
     * 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송(결과 반환) + 인증키 서버로 다시 보내기
     *
     * @param phone  인증 전화번호
     * @param reason 인증 사유
     */
    public SendCertificationNotifyResult sendCertificationMessage(String phone, CertificationReason reason) {
        String certKey = Integer.toString(getTempKey());
//        AwsSmsUtil.sendCertificationMessage(certKey, phone);
        CoolSmsMessageUtil.sendCertificationMessage(certKey, phone);

        CertificationData data = certificationRepository.findByCertificationIdAndType(phone, CertificationType.PHONE);

        if (data != null) {
            data.setCertKey(certKey);
            data.setReason(reason);
            data.setExpiration(LocalDateTime.now().plusMinutes(3));
        } else {
            data = CertificationData.builder()
                    .certificationId(phone)
                    .type(CertificationType.PHONE)
                    .certKey(certKey)
                    .reason(reason)
                    .expiration(LocalDateTime.now().plusMinutes(3))
                    .build();

            certificationRepository.save(data);
        }

        return new SendCertificationNotifyResult(Boolean.TRUE, new CertificationDataDto(data));
    }

    /**
     * @param toMail 인증 이메일
     * @param reason 인증 사유
     */
    public SendCertificationNotifyResult sendCertificationMail(String toMail, CertificationReason reason) {
        if (environment.matchesProfiles("prod")) {
            try {
                log.info("localhost: {}", InetAddress.getLocalHost());
                log.info("localhost의 host address: {}", InetAddress.getLocalHost().getHostAddress());
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else if (environment.matchesProfiles("dev")) {
            host = "localhost";
            port = 8077;
        }

        String certKey = tempKey.getKey(10, false);
        Member member = memberRepository.findByIdentifier(toMail);

        String title = "MovieSite 회원가입 인증 메일";
        String content = "<div style=\"text-align:center\">"
                + "<img src=\"http://" + host + ":" + port + "/images/banner-sign-up2.jpg\" width=\"600\"><br>"
                + "<p>안녕하세요 " + member.getName() + "님. 본인이 가입하신것이 맞다면 다음 링크를 눌러주세요. (링크는 10분간 유효합니다.)</p>"
                + "인증하기 링크 : <a href='http://" + host + ":" + port + "/sign-in?cert=mail&memNo=" + member.getMemNo() + "&certKey=" + certKey + "'>인증하기</a>"
                + "</div>";

        Boolean sendResult = sendEmail(toMail, title, content);
        if (sendResult) {
            CertificationData data = certificationRepository.findByCertificationIdAndType(toMail, CertificationType.EMAIL);
            if (data != null) data.setCertKey(certKey); // 변경 감지
            else {
                data = CertificationData.builder()
                        .certificationId(toMail)
                        .type(CertificationType.EMAIL)
                        .certKey(certKey)
                        .reason(reason)
                        .expiration(LocalDateTime.now().plusMinutes(10)).build();
                certificationRepository.save(data);
            }
            return new SendCertificationNotifyResult(sendResult, new CertificationDataDto(data));
        }

        return new SendCertificationNotifyResult(sendResult, new CertificationDataDto());
    }

    protected Boolean sendEmail(String toMail, String title, String content) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.user", mailUserName);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUserName, mailPassword);
                }
            };

            Session session = Session.getInstance(props, authenticator);
            session.setDebug(true);

            Message msg = new MimeMessage(session);

            Address toAddr = new InternetAddress(toMail);
            Address fromAddr = new InternetAddress(mailUserName);

            msg.setFrom(fromAddr);
            msg.setRecipient(Message.RecipientType.TO, toAddr);
            msg.setSubject(title);
            msg.setContent(content, "text/html; charset=utf-8");
            msg.setSentDate(new Date());

            Transport.send(msg);

            return Boolean.TRUE;
        } catch (MessagingException e) {
            e.fillInStackTrace();
            Exception ex = null;
            if ((ex = e.getNextException()) != null) {
                ex.fillInStackTrace();
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
