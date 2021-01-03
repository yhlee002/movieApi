package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.movie.ThumbImg;
import com.portfolio.demo.project.repository.MovieImgRepository;
import com.portfolio.demo.project.util.DailyBoxOfficeListUtil;
import com.portfolio.demo.project.util.MovieInfoUtil;
import com.portfolio.demo.project.util.NaverMovieInfoUtil;
import com.portfolio.demo.project.vo.movie.MovieDetailVO;
import com.portfolio.demo.project.vo.movie.MovieElementVO;
import com.portfolio.demo.project.vo.movie.MovieVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Service
public class BoxOfficeService {

    private static ResourceBundle bundle = null;

    private final static String MOVIELIST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";

    private static String KEY = null;

    @Autowired
    DailyBoxOfficeListUtil dailyBoxOfficeListUtil;

    @Autowired
    MovieInfoUtil movieInfoUtil;

    @Autowired
    NaverMovieInfoUtil naverMovieInfoUtil;

    @Autowired
    MovieImgRepository movieImgRepository;

    @PostConstruct
    public void initialize() {
        bundle = ResourceBundle.getBundle("Res_ko_KR_keys");
        KEY = bundle.getString("boxOfficeKey");
    }

    public List<MovieVO> getDailyBoxOfficeList() {
        List<MovieVO> movieList = dailyBoxOfficeListUtil.getMovieList(KEY);
//        for (MovieVO movie : movieList) {
//            ThumbImg img = movieImgRepository.findByMovieCd(movie.getMovieCd());
//            movie.setImgUrl(img.getImageUrl());
//        }
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
