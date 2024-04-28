package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.MovieService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.vo.tmdb.MovieDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MovieInfoApi {

    private final MovieService movieService;

    private final CommentMovService commentMovService;

    @Deprecated
    @GetMapping("/movieInfo/{movieCd}")
    public String movieDetail(@PathVariable String movieCd, Model model) {
        Map<String, Object> result = movieService.getMovieInfo(movieCd);

        model.addAttribute("movie", result.get("movie"));
        model.addAttribute("nations", result.get("nations"));
        model.addAttribute("genres", result.get("genres"));
        model.addAttribute("directors", result.get("directors"));
        model.addAttribute("filePath", MovieService.TMDB_IMAGE_PATH);

//        String movieImgUrl = movieService.getMovieImg(movieInfo.getMovieNm());
//        model.addAttribute("movieThumnailUrl", movieImgUrl);

        return "movieInfo/movieInfo";
    }

    @GetMapping("/movieInfo/search")
    public String movieSearch(Model model, @RequestParam("q") String query, @RequestParam("includeAdult") Boolean includeAdult,
                              @RequestParam("page") Integer page, @RequestParam("year") String year) {
        List<com.portfolio.demo.project.vo.tmdb.MovieVO> movieList = movieService.getMovieListByTitle(query, includeAdult, page, year);
        model.addAttribute("movieList", movieList);
        model.addAttribute("query", query);
        model.addAttribute("filePath", MovieService.TMDB_IMAGE_PATH);

        return "movieInfo/searchResult";
    }
}
