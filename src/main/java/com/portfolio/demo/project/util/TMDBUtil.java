package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.tmdb.ImageVO;
import com.portfolio.demo.project.vo.tmdb.MovieDetailVO;
import com.portfolio.demo.project.vo.tmdb.MovieVO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class TMDBUtil {

    private static final Gson gson = new Gson();

    private String KEY = null;
    private String ACCESS_TOKEN = null;

    private final String language = "ko-KR";
    private final String region = "ko";

    public void setKey(String key, String token) {
        this.KEY = key;
        this.ACCESS_TOKEN = token;
    }

    /**
     * HTTP request 전송
     */
    public String sendRequest(String subUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/" + subUrl))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("요청 URI : {}(method: GET)", response.uri());
            log.info("조회해온 정보 : {}", response.body());

            return response.body();
        } catch (IOException | InterruptedException e) {
            log.info("영화 정보 API를 호출하던 중 문제가 생겼습니다.");
        }

        return null;
    }

    public JsonObject parse(String text) {
        return gson.fromJson(text, JsonObject.class);
    }

    /**
     * 영화 id를 이용해 단건 조회
     */
    public MovieDetailVO getMovieDetail(String id) {
        String url = "movie/" + id +
                "?language=" + language;
        String response = sendRequest(url);

        return gson.fromJson(response, MovieDetailVO.class);
    }

    /**
     * 영화에 관련된 이미지 조회
     */
    public Map<String, Object> getMovieImages(String id) {
        String url = "movie/" + id + "/images";
        String response = sendRequest(url);
        Map<String, Object> images = gson.fromJson(response, new TypeToken<Map<String, Object>>() {}.getType());

        List<ImageVO> backDrops = (List<ImageVO>) images.get("backdrops");
        List<ImageVO> logos = (List<ImageVO>) images.get("logos");
        List<ImageVO> posters = (List<ImageVO>) images.get("posters");

        log.info("영화 이미지 조회(영화 ID : {}, backdrop 수 : {}, logo 수 : {}, poster 수 : {})",
                id, backDrops.size(), logos.size(), posters.size());

        return images;
    }

    /**
     * 현재 상영중인 영화 목록 조회
     */
    public List<MovieVO> getNowPlayingMovies(int page) {
        String url = "movie/top_rated?" +
                "language=" + language +
                "&region=" + region +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("상영 예정 영화 목록 조회(결과 레코드 수 : {})", result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovieVO>>() {
        }.getType());
    }

    /**
     * Top Rated 영화 목록 조회
     */
    public List<MovieVO> getTopRatedMovies(int page) {
        String url = "movie/top_rated?" +
                "language=" + language +
                "&region=" + region +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("인기 영화 목록 조회(페이지 : {}, 결과 레코드 수 : {})", page, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovieVO>>() {
        }.getType());
    }

    /**
     * 인기 영화 목록 조회
     */
    public List<MovieVO> getPopularMovies(int page) {
        String url = "movie/popular?" +
                "language=" + language +
                "&region=" + region +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("인기 영화 목록 조회(페이지 : {}, 결과 레코드 수 : {})", page, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovieVO>>() {
        }.getType());
    }

    /**
     * 상영 예정 영화 목록 조회
     */
    public List<MovieVO> getUpcomingMovies(int page) {
        String url = "movie/upcoming?" +
                "language=" + language +
                "&page=" + page;
        JsonObject response = parse(sendRequest(url));

        JsonObject dates = response.get("dates").getAsJsonObject();
        String maximum = dates.get("maximum").getAsString();
        String minimum = dates.get("minimum").getAsString();
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("상영 예정 영화 목록 조회(기간 : {} - {}, 결과 레코드 수 : {})", minimum, maximum, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovieVO>>() {
        }.getType());
    }

    /**
     * 영화 제목으로 검색
     * String primary_release_year은 제외
     */
    public List<MovieVO> getMoviesByTitle(String query, Boolean includeAdult, Integer page, String year) {
        String url = "search/movie?query=" + query + "&language=" + language + "&region=" + region;
        if (includeAdult != null) url += "&include_adult=" + includeAdult;
        if (page != null) url += "&page=" + page;
        if (year != null) url += "&year=" + year;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("영화 제목 검색(키워드 : {}, 결과 레코드 수 : {})", query, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovieVO>>() {
        }.getType());
    }

}
