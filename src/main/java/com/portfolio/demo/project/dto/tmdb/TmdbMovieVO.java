package com.portfolio.demo.project.dto.tmdb;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString
public class TmdbMovieVO {
    private boolean adult; // 관람 등급 성인 여부
    private String backdrop_path; // backdrop_path
    private int[] genre_ids; // genre_ids // 장르 아이디 목록
    private String id;
    private String original_language; // original_language
    private String original_title; // original_title
    private String overview;
    private double popularity;
    private String poster_path; // poster_path
    private String release_date; // release_date
    private String title;
    private boolean video;
    private double vote_average; // vote_average
    private int vote_count; // vote_count
}