package com.portfolio.demo.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 엔티티를 상속한 엔티티들은 이 엔티티의 필드를 모두 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "reg_dt", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS", timezone = "Asia/Seoul")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss:SSS")
    private LocalDateTime regDate;
}
