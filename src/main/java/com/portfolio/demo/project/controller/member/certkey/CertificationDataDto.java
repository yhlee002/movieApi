package com.portfolio.demo.project.controller.member.certkey;

import com.portfolio.demo.project.entity.CertificationData;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificationDataDto {

    private Long id;

    @NotEmpty
    private String certificationId;

    @NotEmpty
    private CertificationType certificationType;

    @NotEmpty
    private String certKey;
    private LocalDateTime expiration;

    public CertificationDataDto(CertificationData data) {
        this.certificationId = data.getCertificationId();
        this.certificationType = data.getType();
        this.certKey = data.getCertKey();
        this.expiration = data.getExpiration();
    }
}