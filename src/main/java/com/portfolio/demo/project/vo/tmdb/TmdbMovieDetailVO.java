package com.portfolio.demo.project.vo.tmdb;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class TmdbMovieDetailVO {
    private boolean adult; // 관람 등급 성인 여부
    private String backdropPath; // backdrop_path
    private String belongsToCollection; // belongs_to_collection // 확실하지 않음!
    private int budget;
    private GenreVO[] genres; // 장르 아이디 목록
    private String homepage;
    private String id;
    private String imdb_id;
    private String originalLanguage; // original_language
    private String originalTitle; // original_title
    private String overview;
    private double popularity;
    private String posterPath; // poster_path

    private ProductionCompanyVO[] production_companies;
    private ProductionContryVO[] production_contries;

    private String releaseDate; // release_date
    private int revenue;
    private int runtime;
    private SpokenLanguageVO[] spoken_languages;
    private String status;
    private String tagline;

    private String title;
    private boolean video;
    private double voteAverage; // vote_average
    private int voteCount; // vote_count
}
