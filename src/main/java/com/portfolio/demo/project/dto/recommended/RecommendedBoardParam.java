package com.portfolio.demo.project.dto.recommended;

import com.portfolio.demo.project.entity.recommended.RecommendedBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Builder
@Setter
@Getter
@ToString
public class RecommendedBoardParam {
    private Long id;
    private Long boardId;
    private Long memNo;
    private String regDate;

    public static RecommendedBoardParam create(RecommendedBoard recommendedBoard) {
        return RecommendedBoardParam.builder()
                .id(recommendedBoard.getId())
                .boardId(recommendedBoard.getBoardId())
                .memNo(recommendedBoard.getMemNo())
                .regDate(recommendedBoard.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
