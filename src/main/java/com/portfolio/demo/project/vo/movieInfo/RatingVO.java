package com.portfolio.demo.project.vo.movieInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class RatingVO { // 전부 '||'를 기준으로 잘라야함
    private String ratingMain; 
    private String ratingDate;
    private String ratingNo;
    private String ratingGrade;
    private String releaseDate;
    private String runtime;
}
