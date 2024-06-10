package com.portfolio.demo.project.controller.member.certkey;

import com.portfolio.demo.project.entity.certification.CertificationReason;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CertMessageValidationRequest {
    @NotEmpty
    private String phone;
    private String certKey;
    private CertificationReason reason;
}