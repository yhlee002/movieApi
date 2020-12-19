package com.portfolio.demo.project.entity.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "oauth_member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OauthMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oauthmemNo; // id

    @Column(name = "UNIQUE_ID", nullable = false)
    private String uniqueId; // api에서 제공하는 uniqueId로 Member entity의 email 컬럼의 역할

    @Column(name = "OAUTHMEM_NAME", nullable = false)
    private String nickname;

//    @Setter
    @Column(name = "PROFILE_IMAGE")
    private String profileImage; // 프로필조회 api에서 제공하는 profile_image를 담아 이를 pojo에 전달할 것

    @Column(name = "OAUTHMEM_PHONE", nullable = false)
    private String phone;

    @Column(name = "REG_DT", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "ROLE") // 회원가입시 ROLE 미부여, 이메일 인증시 ROLE_
    private String role;

    @Column(name = "PROVIDER", nullable = false)
    private String provider; // naver, google, kakao등 정보 제공사

    @Builder
    public OauthMember(Long oauthmemNo, String uniqueId, String nickname, String profileImage, String phone, LocalDateTime regDt, String role, String provider) {
        this.oauthmemNo = oauthmemNo;
        this.uniqueId = uniqueId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.phone = phone;
        this.regDt = regDt;
        this.role = role;
        this.provider = provider;
    }

}