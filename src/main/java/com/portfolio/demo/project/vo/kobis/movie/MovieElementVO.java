package com.portfolio.demo.project.vo.kobis.movie;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
/* 영화 리스트 조회 api */
public class MovieElementVO {
    private String movieCd;
    private String movieNm;
    private String movieNmEn;
    private String prdtYear;
    private String openDt;
    private String typeNm;
    private String prdtStatNm;
    private String nationAlt;
    private String genreAlt;
    private String repNationNm;
    private String repGenreNm;
    private List<DirectorVO> directors;
    private List<CompanyVO> companys; // companyCd, companyNm
}
