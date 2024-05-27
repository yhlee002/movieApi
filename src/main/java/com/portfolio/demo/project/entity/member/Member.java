package com.portfolio.demo.project.entity.member;

import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "member")
@Entity
@Getter
@ToString(exclude = "certKey")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위해 추가
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임(id값을 null로 전달할 경우 DB가 알아서 AUTO_INCREMENT)
    private Long memNo;

    private String identifier;

    private String name;

    @Column(name = "pwd") // 외부 api 가입 회원의 경우 패스워드 불필요
    private String password;

    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    private String provider;

    private String role; // 회원가입시 ROLE 미부여, 이메일 인증시 ROLE_USER

    @Column(name = "certification", columnDefinition = "DEFAULT 'N'")
    private String certification;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "writer")
    List<CommentImp> comments = new ArrayList<>();

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

    public void updateCertification(String certification) {
        this.certification = certification;
    }

    public void updateComments(List<CommentImp> comments) {
        this.comments = comments;
    }
}
