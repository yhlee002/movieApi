package com.portfolio.demo.project.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Builder
@Setter
@Getter
@ToString
public class MemberParam {
    private Long memNo;
    private String identifier;
    @JsonIgnore
    private String password;
    private String name;
    private String profileImage;
    private String phone;
    private String regDate;
    private MemberRole role;
    private String provider;
    private MemberCertificated certification;

    public static MemberParam create(Member member) {
        return MemberParam.builder()
                .memNo(member.getMemNo())
                .identifier(member.getIdentifier())
                .password(member.getPassword())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .phone(member.getPhone())
                .regDate(
                        member.getRegDate() == null ? null :
                                member.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .role(member.getRole())
                .provider(member.getProvider())
                .certification(member.getCertification())
                .build();
    }
}
