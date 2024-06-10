package com.portfolio.demo.project.service;

import com.portfolio.demo.project.util.MovieUtil;
import com.portfolio.demo.project.dto.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.dto.kobis.movie.KobisMovieVO;
import com.portfolio.demo.project.dto.tmdb.ImageConfigurationVO;
import com.portfolio.demo.project.dto.tmdb.TmdbMovieDetailVO;
import com.portfolio.demo.project.dto.tmdb.TmdbMovieVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
@Service
public class MovieService {

    private final MovieUtil movieUtil = new MovieUtil();

    private final ResourceBundle bundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final String KOBIS_KEY = bundle.getString("boxOfficeKey");
    private final String TMDB_KEY = bundle.getString("apiKey");
    private final String TMDB_ACCESS_TOKEN = bundle.getString("apiAccessToken");
    private final String KMDB_kEY = bundle.getString("kmdbApiKey");
    public static final String TMDB_IMAGE_PATH = "v"; // + file size / file path

    private LocalDateTime today = LocalDateTime.now();
    private String targetDt = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 일일 박스오피스
    private String minus1Dt = today.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 일일 박스오피스(전날)
    private int dayOfWeek = today.getDayOfWeek().getValue();
    private String weeklyTargetDt = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 주말 박스오피스
    private String weeklyTargetDt2 = today.minusDays(dayOfWeek).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 주말 박스오피스(저번주)

    {
        movieUtil.setKobisKey(KOBIS_KEY);
        movieUtil.setTmdbKey(TMDB_ACCESS_TOKEN);
        movieUtil.setKmdbKey(KMDB_kEY);

        if (dayOfWeek < 5) {
            LocalDateTime minusDt = today.minusDays(dayOfWeek);
            weeklyTargetDt = minusDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }

    public List<KobisMovieVO> getDailyBoxOfficeList() {
        List<KobisMovieVO> list = movieUtil.getDailyBoxOfficeMovies(targetDt);
        log.info("일간 박스오피스 기준일 : {}", targetDt);
        if (!list.isEmpty()) return list;
        else return movieUtil.getDailyBoxOfficeMovies(minus1Dt);
    }

    public List<KobisMovieVO> getWeeklyBoxOfficeList() {
        List<KobisMovieVO> list = movieUtil.getWeeklyBoxOfficeMovies(targetDt);
        log.info("주간 박스오피스 기준일 : {}", weeklyTargetDt);
        if (!list.isEmpty()) return list;
        else return movieUtil.getWeeklyBoxOfficeMovies(weeklyTargetDt2);
    }

    /**
     * 단건 영화 정보 상세 조회(Kobis)
     * @param movieCd
     * @return
     */
    public Map<String, Object> getMovieInfo(String movieCd) {
        return movieUtil.getMovieInfo(movieCd);
    }


    public ImageConfigurationVO getTmdbConfigurationDetails() {
        return movieUtil.getTmdbConfigurationDetails();
    }

    /**
     * 단건 영화 정보 상세 조회(KMDb)
     * @param title
     * @param director
     * @param releaseYear
     */
    public KmdbMovieDetailVO getMovieDetail(String title, String director, String releaseYear) {
        return movieUtil.getMovieDetail(title, director, releaseYear);
    }

    /**
     * 단건 영화 정보 상세 조회(TMDB)
     *
     * @param movieId
     * @return
     */
    public TmdbMovieDetailVO getMovieDetail(String movieId) {
        return movieUtil.getMovieDetail(movieId);
    }

    /**
     * 영화 이미지 조회(TMDB)
     *
     * @param movieId
     * @return
     */
    public Map<String, Object> getMovieImages(String movieId) {
        return movieUtil.getMovieImages(movieId);
    }

    /**
     * 현재 상영중인 영화 조회(TMDB)
     *
     * @param page
     * @return
     */
    public List<TmdbMovieVO> getNowPlayingMovies(int page) {
        return movieUtil.getNowPlayingMovies(page);
    }

    /**
     * 최고 평점의 영화 조회(TMDB)
     *
     * @param page
     * @return
     */
    public List<TmdbMovieVO> getTopRatedMovies(int page) {
        return movieUtil.getTopRatedMovies(page);
    }

    /**
     * 인기 영화 조회(TMDB)
     *
     * @param page
     * @return
     */
    public List<TmdbMovieVO> getPopularMovies(int page) {
        return movieUtil.getPopularMovies(page);
    }

    /**
     * 개봉 예정인 영화 조회(TMDB)
     *
     * @param page
     * @return
     */
    public List<TmdbMovieVO> getUpComingMovies(int page) {
        return movieUtil.getUpcomingMovies(page);
    }

    /**
     * 영화 제목으로 검색(TMDB)
     *
     * @param query
     * @param includeAdult
     * @param page
     * @param year
     * @return
     */
    public List<TmdbMovieVO> getMovieListByTitle(String query, Boolean includeAdult, Integer page, String year) {
        return movieUtil.getMoviesByTitle(query, includeAdult, page, year);
    }
}
