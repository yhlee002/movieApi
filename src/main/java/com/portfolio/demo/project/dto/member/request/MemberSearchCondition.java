package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberSearchCondition {
    private String identifier;
    private String name;
    private String phone;
    private MemberRole role;
    private MemberCertificated certification;
    private SocialLoginProvider provider;
}
