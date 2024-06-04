package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.controller.member.certkey.CertResponse;
import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.entity.certification.CertificationType;
import com.portfolio.demo.project.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CertificationApi {

    private final CertificationService certificationService;
    /**
     * 인증정보 조회
     *
     * @param certId
     * @param certificationId
     * @param certificationType
     */
    @GetMapping("/certification")
    public ResponseEntity<Result<CertResponse>> getCertification(@RequestParam(required = false) Long certId,
                                                                 @RequestParam(required = false) String certificationId,
                                                                 @RequestParam(required = false) CertificationType certificationType) {
        CertificationDataDto data = null;
        if (certId != null) {
            data = certificationService.findById(certId);
        } else {
            data = certificationService.findByCertificationIdAndType(certificationId, certificationType);
        }

        if (data != null) {
            CertResponse response = new CertResponse(data.getCertificationId(), data.getCertificationType(), data.getCertKey(), null, "");
            return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Result<>(null), HttpStatus.OK);
        }
    }
}
