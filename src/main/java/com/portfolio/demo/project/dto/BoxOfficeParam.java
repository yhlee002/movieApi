package com.portfolio.demo.project.dto;

import com.portfolio.demo.project.dto.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.dto.kobis.movie.KobisMovieVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoxOfficeParam {
    private KobisMovieVO movie;
    private KmdbMovieDetailVO detail;
}
