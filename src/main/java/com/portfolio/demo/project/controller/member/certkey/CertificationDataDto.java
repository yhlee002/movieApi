package com.portfolio.demo.project.controller.member.certkey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.certification.CertificationData;
import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.certification.CertificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CertificationDataDto {

    private Long id;

    @NotEmpty
    private String certificationId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CertificationType certificationType;

    @NotEmpty
    private String certKey;

    @Enumerated(EnumType.STRING)
    private CertificationReason reason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime expiration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime regDate;

    public CertificationDataDto(CertificationData data) {
        this.id = data.getId();
        this.certificationId = data.getCertificationId();
        this.certificationType = data.getType();
        this.certKey = data.getCertKey();
        this.reason = data.getReason();
        this.expiration = data.getExpiration();
        this.regDate = data.getRegDate();
    }
}