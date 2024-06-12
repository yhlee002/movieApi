package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateMemberRequest {
    @NotEmpty
    private String identifier;
    private String password;
    @NotEmpty
    private String name;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String provider;
    private MemberRole role;
    private MemberCertificated certification;
}
