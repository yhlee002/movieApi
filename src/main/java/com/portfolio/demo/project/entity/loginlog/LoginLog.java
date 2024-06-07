package com.portfolio.demo.project.entity.loginlog;

import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "login_log")
@Entity
@Builder
@Setter
@Getter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no")
    private Member member;

    private String ip;

    @Enumerated(EnumType.STRING)
    private LoginResult result;
}
