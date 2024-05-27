package com.portfolio.demo.project.entity;

import com.portfolio.demo.project.controller.member.certkey.CertificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificationData {

    @Id
    private Long id;

    private String certificationId;

    private CertificationType type;

    private String certKey;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiration;
}