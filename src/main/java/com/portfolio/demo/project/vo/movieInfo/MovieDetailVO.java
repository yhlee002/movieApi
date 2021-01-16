package com.portfolio.demo.project.vo.movieInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class MovieDetailVO {
    private String DOCID;
    private String movieId;
    private String movieSeq;
    private String title;
    private String titleEng;
    private String titleEtc;
    private String prodYear;
    private List<DirectorVO> directors;
    private List<ActorVO> actors;
    private String nation;
    private String company;
    private List<PlotVO> plots;
    private String runtime;
    private String rating;
    private String genre;
    private String kmdbUrl;
    private List<RatingVO> ratings;
    private String posters; // '|'를 기준으로 잘라야함
    private String stlls; // '|'를 기준으로 잘라야함
}
