package com.portfolio.demo.project.dto.member;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberResponse {
    private Long memNo;
    private String identifier;
    private String name;
    private String phone;
    private SocialLoginProvider provider;
    private String profileImage;
    private MemberRole role;
    private MemberCertificated certification;
    private String regDate;

    public MemberResponse(MemberParam member) {
        this.memNo = member.getMemNo();
        this.identifier = member.getIdentifier();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.provider = member.getProvider();
        this.profileImage = member.getProfileImage();
        this.role = member.getRole();
        this.certification = member.getCertification();
        this.regDate = member.getRegDate();
    }
}