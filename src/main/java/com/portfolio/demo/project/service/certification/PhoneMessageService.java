package com.portfolio.demo.project.service.certification;

import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import com.portfolio.demo.project.controller.member.certkey.CertificationType;
import com.portfolio.demo.project.entity.CertificationData;
import com.portfolio.demo.project.repository.CertificationRepository;
import com.portfolio.demo.project.util.AwsSmsUtil;
import com.portfolio.demo.project.util.VonageMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhoneMessageService {

    private final VonageMessageUtil messageUtil;

    private final CertificationRepository certificationRepository;

    // 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송(결과 반환) + 인증키 서버로 다시 보내기
    public SendCertificationNotifyResult sendCertificationMessage(String phone) {
        Map<String, String> resultMap = new HashMap<>();
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

    // 랜덤 키 생성
    private int getTempKey() {
        Random ran = new Random();
        return ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성
    }
}
