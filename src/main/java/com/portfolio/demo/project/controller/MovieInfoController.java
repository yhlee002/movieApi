package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.MovieService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.vo.kobis.movie.MovieDetailVO;
import com.portfolio.demo.project.vo.naver.NaverMovieDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MovieInfoController {

    private final MovieService movieService;

    private final CommentMovService commentMovService;

    @GetMapping("/movieInfo/{movieCd}")
    public String movieDetail(@PathVariable String movieCd, Model model) {
        MovieDetailVO movieInfo = movieService.getMovieInfo(movieCd);
        model.addAttribute("movie", movieInfo);

        String movieImgUrl = movieService.getMovieImg(movieInfo.getMovieNm());
        model.addAttribute("movieThumnailUrl", movieImgUrl);

        return "movieInfo/movieInfo";
    }

    // 영화 검색시 네이버 api를 통해 검색 결과 나열
    @GetMapping("/movieInfo/search")
    public String movieSearch(Model model, @RequestParam(name = "q") String query) {
        List<NaverMovieDetailVO> movieList = movieService.getMovieListByTitle(query);
        model.addAttribute("movieList", movieList);
        model.addAttribute("query", query);
        return "movieInfo/searchResult";
    }
}
