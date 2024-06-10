package com.portfolio.demo.project.service.certification;

import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import lombok.Builder;
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
