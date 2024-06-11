package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberCertificated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCertificationRequest {
    @NotNull
    private Long memNo;
    @NotNull
    private MemberCertificated certification;
}
