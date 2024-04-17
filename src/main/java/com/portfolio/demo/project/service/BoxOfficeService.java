package com.portfolio.demo.project.service;

import com.portfolio.demo.project.util.DailyBoxOfficeListUtil;
import com.portfolio.demo.project.util.MovieInfoUtil;
import com.portfolio.demo.project.util.NaverMovieInfoUtil;
import com.portfolio.demo.project.vo.kobis.movie.MovieDetailVO;
import com.portfolio.demo.project.vo.kobis.movie.MovieVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoxOfficeService {

    private static ResourceBundle bundle = null;

    private final static String MOVIELIST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";

    private static String KEY = null;

    private final DailyBoxOfficeListUtil dailyBoxOfficeListUtil;

    private final MovieInfoUtil movieInfoUtil;

    private final NaverMovieInfoUtil naverMovieInfoUtil;

    @PostConstruct
    public void initialize() {
        bundle = ResourceBundle.getBundle("Res_ko_KR_keys");
        KEY = bundle.getString("boxOfficeKey");
    }

    public List<MovieVO> getDailyBoxOfficeList() {
        List<MovieVO> movieList = dailyBoxOfficeListUtil.getMovieList(KEY);
        return movieList;
    }

    public MovieDetailVO getMovieInfo(String movieCd) {
        return movieInfoUtil.getMovieInfo(KEY, movieCd);
    }

    /* 제거 예정 */
    public String getMovieImg(String movieNm) {
        return naverMovieInfoUtil.getMovieThumnailImg(movieNm);
    }

}
