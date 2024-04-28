package com.portfolio.demo.project.vo.kobis.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BoxOfficeResultVO {
    private String boxofficeType;
    private String showRange;
    private String yearWeekTime; // 조회일자에 해당하는 연도와 주차 ex. 2024|
    private List<MovieVO> dailyBoxOfficeList;
    private List<MovieVO> weeklyBoxOfficeList;

}
