package com.portfolio.demo.project.vo.kobis.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class MovieVO {
    private Integer rnum; // 순번을 출력
    private Integer rank; // 해당일자의 박스오피스 순위
    private Integer rankInten; // 전일대비 순위의 증감분
    private String rankOldAndNew; // 랭킹에 신규진입여부 (OLD : 기존, NEW : 신규)
    private Integer movieCd; // 영화의 대표코드
    private String movieNm; // 영화명(국문)
    private String openDt; // 영화의 개봉일
    private Double salesAmt; // 해당일의 매출액
    private Double salesShare; // 해당일자 상영작의 매출총액 대비 해당 영화의 매출비율
    private Double salesInten; // 전일 대비 매출액 증감분
    private Double salesChange; // 전일 대비 매출액 증감 비율
    private Double salesAcc; // 누적매출액
    private Integer audiCnt; // 해당일의 관객수
    private Integer audiInten; // 전일 대비 관객수 증감분
    private Double audiChange; // 전일 대비 관객수 증감 비율
    private Integer audiAcc; // 누적관객수
    private Integer scrnCnt; // 해당일자에 상영한 스크린수
    private Integer showCnt; // 해당일자에 상영된 횟수
}
