package com.portfolio.demo.project.dto.certification;

import lombok.Data;

@Data
public class SendCertificationNotifyResult {

    private Boolean result;
    private CertificationDataDto certificationDataDto;

    public SendCertificationNotifyResult(Boolean result, CertificationDataDto certificationDataDto) {
        this.result = result;
        this.certificationDataDto = certificationDataDto;
    }
}
