package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.MovieService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.vo.BoxOfficeVO;
import com.portfolio.demo.project.vo.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.vo.kobis.movie.KobisMovieVO;
import com.portfolio.demo.project.vo.tmdb.ImageConfigurationVO;
import com.portfolio.demo.project.vo.tmdb.TmdbMovieVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
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

    @GetMapping("/movie/search")
    public ResponseEntity<List<TmdbMovieVO>> movieSearch(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "includeAdult", required = false, defaultValue = "true") Boolean includeAdult,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "year", required = false) String year) {
        String q = URLDecoder.decode(query);
        List<TmdbMovieVO> movies = movieService.getMovieListByTitle(q, includeAdult, page, year);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    /**
     * Kobis 영화 단건 조회
     *
     * @param movieCd
     */
    @GetMapping("/movie/k/{movieCd}")
    public ResponseEntity<Map<String, Object>> kobisMovie(@PathVariable String movieCd) {
        Map<String, Object> result = movieService.getMovieInfo(movieCd);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/movie/boxoffice/daily")
    public ResponseEntity<List<BoxOfficeVO>> dailyBoxOffice() {
        List<KobisMovieVO> list = movieService.getDailyBoxOfficeList();

        List<BoxOfficeVO> result = new ArrayList<>();
        for (KobisMovieVO movieVO : list) {
            KmdbMovieDetailVO detail = movieService.getMovieDetail(movieVO.getMovieNm(), null, movieVO.getOpenDt());
            result.add(BoxOfficeVO.builder()
                    .movie(movieVO)
                    .detail(detail)
                    .build());
        }
        log.info("조회된 주간 박스오피스(데이터 수 : {})", result.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/movie/boxoffice/weekly")
    public ResponseEntity<List<BoxOfficeVO>> weeklyBoxOffice() {
        List<KobisMovieVO> list = movieService.getWeeklyBoxOfficeList();

        List<BoxOfficeVO> result = new ArrayList<>();

        for (KobisMovieVO movieVO : list) {
            KmdbMovieDetailVO detail = movieService.getMovieDetail(movieVO.getMovieNm(), null, movieVO.getOpenDt());
            result.add(BoxOfficeVO.builder()
                    .movie(movieVO)
                    .detail(detail)
                    .build());
        }
        log.info("조회된 주말 박스오피스(데이터 수 : {})", list.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * TMDB 영화 API 설정 조회
     */
    @GetMapping("/movie/t/configuration")
    public ResponseEntity<ImageConfigurationVO> getTmdbConfigurationDetails() {
        return new ResponseEntity<>(movieService.getTmdbConfigurationDetails(), HttpStatus.OK);
    }

    /**
     * TMDB 영화 단건 조회
     *
     * @param movieId
     */
//    @GetMapping("/movie/t/{movieId}")
//    public ResponseEntity<TmdbMovieDetailVO> tmdbMovie(@PathVariable String movieId) {
//        TmdbMovieDetailVO result = movieService.getMovieDetail(movieId);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    /**
     * KMDb 영화 단건 조회(포스터, 줄거리 등)
     */
}
