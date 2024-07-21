package com.portfolio.demo.project.dto.member.request;

import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSearchCondition {
    private MemberRole role;
    private MemberCertificated certification;
    private SocialLoginProvider provider;
    private String identifier;
    private String name;
    private String phone;
}
