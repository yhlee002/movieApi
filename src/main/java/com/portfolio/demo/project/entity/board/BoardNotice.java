package com.portfolio.demo.project.entity.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "board_notice")
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Column(name = "content", nullable = false)
    private String content; // 내용

    @Column(name = "mod_dt")
    @LastModifiedDate
    private String modDate; // 최종 수정 시간

    @Column(name = "views")
    private int views = 0; // 조회수

    public void updateViewCnt() {
        this.views++;
    }
}
