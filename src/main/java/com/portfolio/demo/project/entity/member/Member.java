package com.portfolio.demo.project.entity.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

//@Data // @Data만 쓰면 NoArgConstructor만 생성
@Table(name = "member")
@Getter
@ToString(exclude = "certKey")
//@AllArgsConstructor // @AllArgsConsturctor을 쓰면 기본 생성자가 없어짐
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위해 추가
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임(id값을 null로 전달할 경우 DB가 알아서 AUTO_INCREMENT)
    private Long memNo;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pwd") // 외부 api 가입 회원의 경우 패스워드 불필요
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "provider")
    private String provider;

    @Column(name = "role") // 회원가입시 ROLE 미부여, 이메일 인증시 ROLE_
    private String role;

    @Column(name = "cert_key")
    private String certKey;

    @Column(name = "certification")
    private String certification;

    @Builder
    public Member(Long memNo, String identifier, String name, String password, String phone, LocalDateTime regDt, String profileImage, String provider, String role, String certKey, String certification) {
        this.memNo = memNo;
        this.identifier = identifier;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.regDt = regDt;
        this.profileImage = profileImage;
        this.provider = provider;
        this.role = role;
        this.certKey = certKey;
        this.certification = certification;
    }
}
