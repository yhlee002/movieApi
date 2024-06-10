package com.portfolio.demo.project.controller.member.certkey;

import com.portfolio.demo.project.entity.certification.CertificationType;
import lombok.Data;

/**
 * 1. 인증번호 생성 및 전송 결과 반환
 * 2. 인증번호 일치 여부 확인 결과 반환
 */

@Data
public class CertResponse {
    private String certificationId;
    private CertificationType certificationType; // [PHONE|EMAIL]
    private String key;
    private Boolean status;
    private String message;

    public CertResponse(String certificationId, CertificationType certificationType, String key, Boolean status, String message) {
        this.certificationId = certificationId;
        this.certificationType = certificationType;
        this.key = key;
        this.status = status;
        this.message = message;
    }
}