package com.portfolio.demo.project.service;

import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import com.portfolio.demo.project.controller.member.certkey.CertificationType;
import com.portfolio.demo.project.entity.CertificationData;
import com.portfolio.demo.project.repository.CertificationRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class CertificationService {

    private final MemberRepository memberRepository;

    private final CertificationRepository certificationRepository;

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
}
