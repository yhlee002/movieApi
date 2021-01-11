package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.BoxOfficeService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.vo.movie.MovieDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MovieInfoController {

    @Autowired
    BoxOfficeService boxOfficeService;

    @Autowired
    CommentMovService commentMovService;

    @RequestMapping("/movieInfo/{movieCd}")
    public String movieDetail(@PathVariable String movieCd, Model model) {
        MovieDetailVO movieInfo = boxOfficeService.getMovieInfo(movieCd);
        model.addAttribute("movie", movieInfo);

        String movieImgUrl = boxOfficeService.getMovieImg(movieInfo.getMovieNm());
        model.addAttribute("movieThumnailUrl", movieImgUrl);

//        String commentStr = commentMovService.getCommentListOrderByRecommended();

        return "movieInfo/movieInfo";
    }
}
