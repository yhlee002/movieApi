package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.dto.kobis.movie.KobisMovieDetailVO;
import com.portfolio.demo.project.dto.kobis.movie.KobisMovieVO;
import com.portfolio.demo.project.dto.tmdb.ImageVO;
import com.portfolio.demo.project.dto.tmdb.TmdbMovieDetailVO;
import com.portfolio.demo.project.dto.tmdb.TmdbMovieVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Test
    void kobis_일간_박스오피스_영화_목록_조회() {
        // given & when
        List<KobisMovieVO> movieList = movieService.getDailyBoxOfficeList();

        for (KobisMovieVO movieVO : movieList) {
            System.out.println(movieVO);
        }

        // then
        Assertions.assertNotNull(movieList);
        Assertions.assertEquals(10, movieList.size());
    }

    @Test
    void kobis_주간_박스오피스_영화_목록_조회() {
        List<KobisMovieVO> movieList = movieService.getWeeklyBoxOfficeList();

        for (KobisMovieVO movieVO : movieList) {
            System.out.println(movieVO);
        }

        // then
        Assertions.assertNotNull(movieList);
        Assertions.assertEquals(10, movieList.size());
    }

    @Test
    void kobis_영화_id를_이용한_단건_조회() {
        // given
        String movieId = "20234675";

        // when
        Map<String, Object> result = movieService.getMovieInfo(movieId);
        KobisMovieDetailVO kobisMovieDetailVO = (KobisMovieDetailVO) result.get("movie");

        // then
        assertEquals("파묘", kobisMovieDetailVO.getMovieNm());
        assertEquals("Exhuma", kobisMovieDetailVO.getMovieNmEn());
        assertFalse(kobisMovieDetailVO.getActors().isEmpty());
    }

    @Test
    void kmdb_영화_조회() {
        // given
        String title = "파묘";
        String director = "장재현";
        String year = "2024";

        // when
        KmdbMovieDetailVO movie = movieService.getMovieDetail(title, director, year);

        // then
        Assertions.assertNotNull(movie);
        Assertions.assertEquals("파묘", movie.getTitle());
        Assertions.assertEquals("장재현", movie.getDirectors().getDirector().get(0).getDirectorNm());
        Assertions.assertEquals("2024", movie.getProdYear());
    }

    @Test
    void tmdb_현재_상영중인_영화_조회() {
        // given * when
        List<TmdbMovieVO> nowPlayingMovies = movieService.getNowPlayingMovies(1);

        // then
        Assertions.assertNotNull(nowPlayingMovies);
    }

    @Test
    void tmdb_최고_평점의_영화_조회() {
        // given & when
        List<TmdbMovieVO> topRatedMovies = movieService.getTopRatedMovies(1);

        // then
        Assertions.assertNotNull(topRatedMovies);
    }

    @Test
    void tmdb_인기_영화_조회() {
        // given & when
        List<TmdbMovieVO> popularMovies = movieService.getPopularMovies(1);

        // then
        Assertions.assertNotNull(popularMovies);
    }

    @Test
    void tmdb_개봉_예정인_영화_조회() {
        // given & when
        List<TmdbMovieVO> upComingMovies = movieService.getUpComingMovies(1);

        // then
        Assertions.assertNotNull(upComingMovies);
    }

    @Test
    void tmdb_영화_id를_이용한_단건_조회() {
        // given & when
        String movieId = "1096197"; // sample id(영화제목 : No Way Up)
        TmdbMovieDetailVO movieDetail = movieService.getMovieDetail(movieId);

        // then
        Assertions.assertNotNull(movieDetail);
    }

    @Test
    void tmdb_영화_이미지_조회() {
        // then
        String movieId = "1096197"; // sample id(영화제목 : No Way Up)

        // when
        Map<String, Object> images = movieService.getMovieImages(movieId);

        int id = (int) Math.ceil((double) images.get("id"));
        List<ImageVO> backDrops = (List<ImageVO>) images.get("backdrops");
        List<ImageVO> logos = (List<ImageVO>) images.get("logos");
        List<ImageVO> posters = (List<ImageVO>) images.get("posters");

        // then
        Assertions.assertEquals(Integer.parseInt(movieId), id);
        Assertions.assertTrue(backDrops.size() > 0);
        Assertions.assertTrue(logos.size() > 0);
        Assertions.assertTrue(posters.size() > 0);
    }

    @Test
    void tmdb_영화_제목으로_검색() {
        // given
        final String keyword = "파이트" .toLowerCase();

        // when
        List<TmdbMovieVO> result = movieService.getMovieListByTitle(keyword, true, 1, null);

        // then
        Assertions.assertNotNull(result);
    }

}