package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.MovieService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.vo.kobis.movie.MovieVO;
import com.portfolio.demo.project.vo.tmdb.MovieDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MovieInfoApi {

    private final MovieService movieService;

    private final CommentMovService commentMovService;

//    @Deprecated
//    @GetMapping("/movieInfo/{movieCd}")
//    public String movieDetail(@PathVariable String movieCd, Model model) {
//        Map<String, Object> result = movieService.getMovieInfo(movieCd);
//
//        model.addAttribute("movie", result.get("movie"));
//        model.addAttribute("nations", result.get("nations"));
//        model.addAttribute("genres", result.get("genres"));
//        model.addAttribute("directors", result.get("directors"));
//        model.addAttribute("filePath", MovieService.TMDB_IMAGE_PATH);
//
//        return "movieInfo/movieInfo";
//    }
//
//    @Deprecated
//    @GetMapping("/movieInfo/search")
//    public String movieSearch(Model model, @RequestParam("q") String query, @RequestParam("includeAdult") Boolean includeAdult,
//                              @RequestParam("page") Integer page, @RequestParam("year") String year) {
//        List<com.portfolio.demo.project.vo.tmdb.MovieVO> movieList = movieService.getMovieListByTitle(query, includeAdult, page, year);
//        model.addAttribute("movieList", movieList);
//        model.addAttribute("query", query);
//        model.addAttribute("filePath", MovieService.TMDB_IMAGE_PATH);
//
//        return "movieInfo/searchResult";
//    }

    /**
     * Kobis 영화 단건 조회
     *
     * @param movieCd
     */
    @GetMapping("/movie/{movieCd}")
    public ResponseEntity<Map<String, Object>> kobisMovie(@PathVariable String movieCd) {
        Map<String, Object> result = movieService.getMovieInfo(movieCd);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/movie/boxoffice/daily")
    public ResponseEntity<List<MovieVO>> dailyBoxOffice() {
        List<MovieVO> list = movieService.getDailyBoxOfficeList();
        log.info("조회된 주간 박스오피스(데이터 수 : {})", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/movie/boxoffice/weekly")
    public ResponseEntity<List<MovieVO>> weeklyBoxOffice() {
        List<MovieVO> list = movieService.getWeeklyBoxOfficeList();
        log.info("조회된 주말 박스오피스(데이터 수 : {})", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * TMDB 영화 단건 조회
     *
     * @param movieId
     */
//    @GetMapping("/movie/{movieId}")
//    public ResponseEntity<com.portfolio.demo.project.vo.tmdb.MovieDetailVO> tmdbMovie(@PathVariable String movieId) {
//        com.portfolio.demo.project.vo.tmdb.MovieDetailVO result = movieService.getMovieDetail(movieId);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
}
