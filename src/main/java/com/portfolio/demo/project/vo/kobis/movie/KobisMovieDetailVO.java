package com.portfolio.demo.project.vo.kobis.movie;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class KobisMovieDetailVO {
    private String movieCd; // 영화코드
    private String movieNm; // 영화명(국문)
    private String movieNmEn; // 영화명(영문) *국문명 뒤에 괄호 치고 붙이기
    private String movieNmOg; // 영화명(원문)
    private String prdtYear; // 제작연도 (+년)
    private String showTm; // 상영시간 (+분)
    private String openDt; // 개봉연도
    private String prdtStatNm; // 제작상태명 (개봉, 미개봉 등)
    private String typeNm; // 영화유형명 (장편, 단편 등)

    /** 제작국가 (nations > nation > nationNm) */
//    private String nations;
    private List<NationVO> nations; // 제작국가명

    /** 장르 (genres > genre > genreNm) */
//    private String genres;
    private List<GenreVO> genres; // 장르명(genreNm)

    /** 감독 (directors > director > peopleNm, peopleNmEn) */
//    private String directors;
    private List<DirectorVO> directors; // 감독명 (원래 key : peopleNm) -> directorNm
//    private String peopleNmEn; // 감독명(영문)

    /** 배우 (actors > actor > peopleNm, peopleNmEn, cast, castEn) */
    private List<ActorVO> actors;
//    private String actors;
//    private String actorNm; // 배우명 (원래 key : peopleNm)  ★
//    private String cast; // 배역명  ★
//    private String castEn; // 배역명(영문)

//    /** 상영형태 구분 (showTypes > showType > showTypeGroupNm, showTypeNm */
//    private String showTypes;
//    private String showTypeGroupNm; // 상영형태 구분
//    private String showTypeNm; // 상영형태명

    /** 심의정보 (audits > audit > autidNo, watchGradeNm) */
//    private String audits;
//    private String auditNo; // 심의번호
    private String watchGradeNm; // 관람등급 명칭  ★

    /** 참여 영화사 (companys > company > companyCd, companyNm, companyNmEnm, companyPartNm) */
    private List<CompanyVO> companys;
//    private String companys;
//    private String companyCd; //  참여 영화사 코드
//    private String companyNm; // 참여 영화사명  ★
//    private String companyNmEn; // 참여 영화사명(영문)
//    private String companyPartNm; // 참여 영화사 분야명 (제작사, 배급사, 제공)  ★

//    /** 스텝들 */
//    private String staffs; // 스텝
//    private String peopleNm; // 스텝명
//    private String peopleNmEn; // 스텝명(영문)
//    private String staffRoleNm; // 스텝역할명


}
