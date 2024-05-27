package com.portfolio.demo.project.controller.member.certkey;

import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class CertKeyApi {

    private final CertificationService certificationService;

//    @GetMapping("/api/certification")
//    public ResponseEntity<Result<CertificationDataDto>> findById(@RequestParam Long id) {
//        CertificationDataDto data = certificationService.findById(id);
//        return new ResponseEntity<>(new Result<>(data), HttpStatus.OK);
//    }

    @GetMapping("/api/certification")
    public ResponseEntity<Result<CertificationDataDto>> findCertificationById(@RequestParam String certificationId, @RequestParam CertificationType type) {
        CertificationDataDto data = certificationService.findByCertificationIdAndType(certificationId, type);
        return new ResponseEntity<>(new Result<>(data), HttpStatus.OK);
    }

    @PostMapping("/api/certification")
    public ResponseEntity<Result<CertificationDataDto>> saveCertification(@RequestBody @Valid CertificationDataDto param) {
        Long id = certificationService.saveCertification(param);
        CertificationDataDto data = certificationService.findById(id);

        return new ResponseEntity<>(new Result<>(data), HttpStatus.OK);
    }

//    @PatchMapping("/api/certification")
//    public void updateCertification(@RequestBody @Valid CertificationDataDto param) {
//        certificationService.updateCertification(param);
//    }

    @DeleteMapping("/api/certification")
    public void deleteCertification(@RequestParam String certificationId, CertificationType type) {
        CertificationDataDto data = certificationService.findByCertificationIdAndType(certificationId, type);
        certificationService.deleteCertification(data);
    }

}
