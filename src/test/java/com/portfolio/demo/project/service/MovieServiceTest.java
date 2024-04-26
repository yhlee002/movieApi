package com.portfolio.demo.project.service;

import com.portfolio.demo.project.vo.naver.NaverMovieDetailVO;
import com.portfolio.demo.project.vo.tmdb.ImageVO;
import com.portfolio.demo.project.vo.tmdb.MovieDetailVO;
import com.portfolio.demo.project.vo.tmdb.MovieVO;
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
    void kobis_전체_영화_목록_조회() {
        // given & when
        List<com.portfolio.demo.project.vo.kobis.movie.MovieVO> movieList = movieService.getDailyBoxOfficeList();

        for (com.portfolio.demo.project.vo.kobis.movie.MovieVO movieVO : movieList) {
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
        com.portfolio.demo.project.vo.kobis.movie.MovieDetailVO movieDetailVO = movieService.getMovieInfo(movieId);

        // then
        assertEquals("파묘", movieDetailVO.getMovieNm());
        assertEquals("Exhuma", movieDetailVO.getMovieNmEn());
        assertFalse(movieDetailVO.getActors().isEmpty());
    }

    @Test
    void tmdb_현재_상영중인_영화_조회() {
        // given * when
        List<MovieVO> nowPlayingMovies = movieService.getNowPlayingMovies(1);

        // then
        Assertions.assertNotNull(nowPlayingMovies);
    }

    @Test
    void tmdb_최고_평점의_영화_조회() {
        // given & when
        List<MovieVO> topRatedMovies = movieService.getTopRatedMovies(1);

        // then
        Assertions.assertNotNull(topRatedMovies);
    }

    @Test
    void tmdb_인기_영화_조회() {
        // given & when
        List<MovieVO> popularMovies = movieService.getPopularMovies(1);

        // then
        Assertions.assertNotNull(popularMovies);
    }

    @Test
    void tmdb_개봉_예정인_영화_조회() {
        // given & when
        List<MovieVO> upComingMovies = movieService.getUpComingMovies(1);

        // then
        Assertions.assertNotNull(upComingMovies);
    }

    @Test
    void tmdb_영화_id를_이용한_단건_조회() {
        // given & when
        String movieId = "1096197"; // sample id(영화제목 : No Way Up)
        MovieDetailVO movieDetail = movieService.getMovieDetail(movieId);

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
        List<MovieVO> result = movieService.getMovieListByTitle(keyword, true, 1, null);

        // then
        Assertions.assertNotNull(result);
    }

}