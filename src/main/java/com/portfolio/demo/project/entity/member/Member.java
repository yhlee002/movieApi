package com.portfolio.demo.project.entity.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

//@Data // @Data만 쓰면 NoArgConstructor만 생성
@Table(name = "member")
@Getter
@ToString
//@AllArgsConstructor // @AllArgsConsturctor을 쓰면 기본 생성자가 없어짐
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위해 추가
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임(id값을 null로 전달할 경우 DB가 알아서 AUTO_INCREMENT)
    private Long memNo; // Mem_No
    @Column(name = "MEM_EMAIL", nullable = false)
    private String email;
    @Column(name = "MEM_NAME", nullable = false)
    private String name;
    @Column(name = "MEM_PWD", nullable = false)
    private String password;
    @Column(name = "MEM_PHONE")
    private String phone;
    @Column(name = "REG_DT", nullable = false)
    private LocalDateTime regDt;
    @Column(name = "ROLE") // 회원가입시 ROLE 미부여, 이메일 인증시 ROLE_
    private String role;

    @Builder
    public Member(Long memNo, String email, String name, String password, String phone, LocalDateTime regDt) {
        this.memNo = memNo;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.regDt = regDt;
    }
}
