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
    private List<MovieVO> dailyBoxOfficeList;

}
