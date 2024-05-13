package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.vo.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.vo.kobis.movie.KobisMovieVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoxOfficeVO {
    private KobisMovieVO movie;
    private KmdbMovieDetailVO detail;
}
