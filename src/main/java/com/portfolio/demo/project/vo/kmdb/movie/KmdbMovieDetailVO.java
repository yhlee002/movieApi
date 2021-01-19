package com.portfolio.demo.project.vo.kmdb.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class KmdbMovieDetailVO {
    private String DOCID;
    private String movieId;
    private String movieSeq;
    private String title;
    private String titleEng;
    private String titleEtc;
    private String prodYear;
    private DirectorVO director;
    private ActorVO actors;
    private String nation;
    private String company;
    private PlotVO plots;
    private String runtime;
    private String rating;
    private String genre;
    private String kmdbUrl;
    private RatingVO ratings;
    private String posters; // '|'를 기준으로 잘라야함
    private String stlls; // '|'를 기준으로 잘라야함
}
