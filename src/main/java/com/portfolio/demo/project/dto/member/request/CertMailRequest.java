package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.certification.CertificationReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertMailRequest {
    @NotEmpty
    private String identifier;
    @NotNull
    private CertificationReason reason;
}
