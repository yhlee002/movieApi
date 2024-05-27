package com.portfolio.demo.project.controller.member.certkey;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CertMessageValidationRequest {
    @NotEmpty
    private String phone;
    @NotEmpty
    private String certKey;
}