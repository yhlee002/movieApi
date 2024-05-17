package com.portfolio.demo.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonSerialize
@AllArgsConstructor
public class ImpressionPagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private List<BoardImpParam> boardImpList; // 한 화면에 보여줄 게시글 개수
}
