package com.portfolio.demo.project.entity.member;

import com.portfolio.demo.project.entity.BaseEntity;
import lombok.*;

import jakarta.persistence.*;

@Table(name = "member")
@Entity
@Getter
@ToString(exclude = "certKey")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위해 추가
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임(id값을 null로 전달할 경우 DB가 알아서 AUTO_INCREMENT)
    private Long memNo;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pwd") // 외부 api 가입 회원의 경우 패스워드 불필요
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "provider")
    private String provider;

    @Column(name = "role") // 회원가입시 ROLE 미부여, 이메일 인증시 ROLE_
    private String role;

    @Column(name = "cert_key")
    private String certKey;

    @Column(name = "certification", columnDefinition = "DEFAULT 'N'")
    private String certification;

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateProvider(String provider) {
        this.provider = provider;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateCertKey(String certKey) {
        this.certKey = certKey;
    }

    public void updateCertification(String certification) {
        this.certification = certification;
    }
}
