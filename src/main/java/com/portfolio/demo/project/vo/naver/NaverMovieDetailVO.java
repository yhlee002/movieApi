package com.portfolio.demo.project.vo.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NaverMovieDetailVO {
    private String title;
    private String link;
    private String image;
    private String subtitle;
    private String pubDate;
    private String director;
    private String actor;
    private String userRating;
}
