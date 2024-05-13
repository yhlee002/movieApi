package com.portfolio.demo.project.vo.kmdb;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class KmdbMovieDetailVO {
    private String DOCID;
    private String movieId;
    private String movieSeq;
    private String title;
    private String titleEng;
    private String titleOrg;
    private String titleEtc;
    private String prodYear; // 제작연도

    private DirectorsVO directors;

    private ActorsVO actors;

    private String nation;
    private String company;

    private PlotsVO plots; // 줄거리

    private String runtime; // 대표상영시간
    private String rating;
    private String genre;
    private String kmdbUrl;
    private String type; // 유형구분 ex. 극영화
    private String use; // 용도구분 ex. 극장용
    private String episodes; // 영상 내 에피소드
    private String ratedYn; // 심의여부
    private String repRatDate; // 대표심의일
    private String repRlsDate; // 대표개봉일

    private RatingsVO ratings;

    private String keywords;

//    private List<String> posters = new ArrayList<>();
    private String posters;
    private String stlls;

    private StaffsVO staffs;

    private String openThtr; // 개봉극장

    private List<StatVO> stat;

    private String screenArea; // 관람지역
    private String screenCnt; // 스크린 수
    private String salesAcc; // 누적 매출액
    private String audiAcc; // 누적 관람인원
    private String statSouce; // 출처
    private String statDate; // 기준일

    private String themeSong; // 주제곡
    private String soundtrack; // 삽입곡
    private String fLocation; // 촬영장소
    private String Awards1; // 영화제 수상내역
    private String Awards2; // 수상내역 기타
    private String regDate; // 등록일
    private String modDate; // 최종수정일

    private CodesVO codes;
    private CodesVO CommCodes; // 대표외부코드

    private String ALIAS;

//    public void setPosters(String posters) {
//        String[] posterArr = posters.split("|");
//        if (posterArr.length > 0) {
//            this.posters = Arrays.asList(posterArr);
//        }
//    }
}
