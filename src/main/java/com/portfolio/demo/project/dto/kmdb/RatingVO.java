package com.portfolio.demo.project.dto.kmdb;

import lombok.Getter;

@Getter
public class RatingVO {
    private String ratingMain; // 대표심의정보 여부
    private String ratingDate; // 심의일
    private String ratingNo; // 심의번호
    private String ratingGrade; // 관람기준
    private String releaseDate; // 개봉일자
    private String runtime; // 상영시간
}
