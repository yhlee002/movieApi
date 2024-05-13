package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.kmdb.KmdbMovieDetailVO;
import com.portfolio.demo.project.vo.kobis.movie.*;
import com.portfolio.demo.project.vo.tmdb.ImageConfigurationVO;
import com.portfolio.demo.project.vo.tmdb.ImageVO;
import com.portfolio.demo.project.vo.tmdb.TmdbMovieDetailVO;
import com.portfolio.demo.project.vo.tmdb.TmdbMovieVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MovieUtil {

    private final Gson gson = new Gson();

    // kobis box-office api
    private final static String DAILYBOXOFFICE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private final static String WEEKLYBOXOFFICE_URL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json";
    private final static String MOVIEINFO_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    private String KOBIS_KEY = null;

    public void setKobisKey(String key) {
        this.KOBIS_KEY = key;
    }

    public List<KobisMovieVO> getDailyBoxOfficeMovies(String targetDt) {
        List<KobisMovieVO> movieList = null;

        try {

            HttpURLConnection con = getConnection(DAILYBOXOFFICE_URL + "?key=" + KOBIS_KEY + "&targetDt=" + targetDt);
            String res = null;

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = getResult(con.getInputStream());

                JsonObject result = gson.fromJson(res, JsonObject.class);
                String boxOfficeResult = result.get("boxOfficeResult").getAsJsonObject().toString();
                BoxOfficeResultVO resultVO = gson.fromJson(boxOfficeResult, BoxOfficeResultVO.class);
                movieList = resultVO.getDailyBoxOfficeList();

            } else { // 에러 발생
                res = getResult(con.getErrorStream());
                log.error(res);
                con.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    public List<KobisMovieVO> getWeeklyBoxOfficeMovies(String targetDt) {
        List<KobisMovieVO> movieList = null;

        try {
            HttpURLConnection con = getConnection(WEEKLYBOXOFFICE_URL + "?key=" + KOBIS_KEY + "&targetDt=" + targetDt);
            String res = null;

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = getResult(con.getInputStream());

                JsonObject result = gson.fromJson(res, JsonObject.class);
                String boxOfficeResult = result.get("boxOfficeResult").getAsJsonObject().toString();
                BoxOfficeResultVO resultVO = gson.fromJson(boxOfficeResult, BoxOfficeResultVO.class);
                movieList = resultVO.getWeeklyBoxOfficeList();

            } else { // 에러 발생
                res = getResult(con.getErrorStream());
                log.error(res);
                con.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    public Map<String, Object> getMovieInfo(String movieCd) {
        Map<String, Object> datas = new HashMap<>();

        try {
            HttpURLConnection con = getConnection(MOVIEINFO_URL + "?key=" + KOBIS_KEY + "&movieCd=" + movieCd);
            String res = null;

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                res = getResult(con.getInputStream());

                JsonObject result = gson.fromJson(res, JsonObject.class);
                JsonObject movieInfoResult = result.get("movieInfoResult").getAsJsonObject();
                JsonObject movieInfo = movieInfoResult.get("movieInfo").getAsJsonObject();

                KobisMovieDetailVO movieDetail = gson.fromJson(movieInfoResult.get("movieInfo").toString(), KobisMovieDetailVO.class); // new TypeToken<ArrayList<MovieVO>>() {}.getType()


                datas.put("movie", movieDetail);
                datas.put("nations", gson.fromJson(movieInfo.get("nations").getAsJsonArray().toString(), new TypeToken<ArrayList<NationVO>>() {
                }.getType()));
                datas.put("genres", gson.fromJson(movieInfo.get("genres").toString(), new TypeToken<ArrayList<GenreVO>>() {
                }.getType()));
                datas.put("directors", gson.fromJson(movieInfo.get("directors").toString(), new TypeToken<ArrayList<DirectorVO>>() {
                }.getType()));

            } else { // 에러 발생
                res = getResult(con.getErrorStream());
                log.error(res);
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public HttpURLConnection getConnection(String apiUrl) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(apiUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public String getResult(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }


    // KMDb
    private String KMDB_KEY = null;

    public void setKmdbKey(String token) {
        this.KMDB_KEY = token;
    }

    public KmdbMovieDetailVO getMovieDetail(String title, String director, String year) {
        StringBuilder urlBuilder = new StringBuilder("https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2");
//        StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2");
        urlBuilder.append("&listCount=1");
        if (title != null) {
            urlBuilder.append("&title=").append(URLEncoder.encode(title, StandardCharsets.UTF_8));
        }
        if (director != null) {
            urlBuilder.append("&director=").append(URLEncoder.encode(director, StandardCharsets.UTF_8));
        }
        if (year != null) {
            urlBuilder.append("&releaseDts=").append(year);
        }
        urlBuilder.append("&ServiceKey=").append(KMDB_KEY);

        HttpURLConnection conn = null;
        BufferedReader rd = null;
        String jsonStr = "";
        int responseCode = 0;
        try {
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            responseCode = conn.getResponseCode();
            log.info("KMDb API 요청 주소: {}", urlBuilder.toString());
            log.info("KMDb API 응답 코드: {}", responseCode);

            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            log.info("KMDb에서 조회해온 영화(제목: {}, 감독: {}, 개봉년도: {}): {}",
                    title, director, year, sb.toString());

            jsonStr = sb.toString();
        } catch (IOException e) {
            try {
                if (rd != null) {
                    rd.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        KmdbMovieDetailVO movie = null;
        if (responseCode == 200) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                JsonObject result = objectMapper.readValue(jsonStr, JsonObject.class);
//                JsonArray datas = result.getAsJsonArray("Data");
//                JsonObject data = datas.get(0).getAsJsonObject();
//                JsonArray results = data.getAsJsonArray("Result");
//                String movieStr = results.get(0).getAsString();
//                movie = objectMapper.readValue(movieStr, KmdbMovieDetailVO.class);
//
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }

            Gson gson = new Gson();
            JsonObject result = gson.fromJson(jsonStr, JsonObject.class);
            JsonArray datas = result.getAsJsonArray("Data");
            JsonObject data = datas.get(0).getAsJsonObject();
            JsonArray results = data.getAsJsonArray("Result");
            String movieStr = results.get(0).toString();
            movie = gson.fromJson(movieStr, KmdbMovieDetailVO.class);
        }

        return movie;
    }


    // TMDB
    private String TMDB_TOKEN = null;

    private final String tmdbLanguage = "ko-KR";
    private final String tmdbRegion = "KR"; // ISO 3166-1

    public void setTmdbKey(String token) {
        this.TMDB_TOKEN = token;
    }

    /**
     * HTTP request 전송
     * @param subUrl
     */
    public String sendRequest(String subUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/" + subUrl))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + TMDB_TOKEN)
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
     * 설정 조회
     * @return
     */
    public ImageConfigurationVO getTmdbConfigurationDetails() {
        String url = "configuration";

        String response = sendRequest(url);

        JsonObject result = gson.fromJson(response, JsonObject.class);
        JsonObject images = result.getAsJsonObject("images");

        return gson.fromJson(images.toString(), ImageConfigurationVO.class);
    }

    /**
     * 영화 id를 이용해 단건 조회
     *
     * @param id
     */
    public TmdbMovieDetailVO getMovieDetail(String id) {
        String url = "movie/" + id +
                "?language=" + tmdbLanguage;
        String response = sendRequest(url);

        return gson.fromJson(response, TmdbMovieDetailVO.class);
    }

    /**
     * 영화에 관련된 이미지 조회
     *
     * @param id
     */
    public Map<String, Object> getMovieImages(String id) {
        String url = "movie/" + id + "/images";
        String response = sendRequest(url);
        Map<String, Object> images = gson.fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());

        List<ImageVO> backDrops = (List<ImageVO>) images.get("backdrops");
        List<ImageVO> logos = (List<ImageVO>) images.get("logos");
        List<ImageVO> posters = (List<ImageVO>) images.get("posters");

        log.info("영화 이미지 조회(영화 ID : {}, backdrop 수 : {}, logo 수 : {}, poster 수 : {})",
                id, backDrops.size(), logos.size(), posters.size());

        return images;
    }

    /**
     * 현재 상영중인 영화 목록 조회
     *
     * @param page
     */
    public List<TmdbMovieVO> getNowPlayingMovies(int page) {
        String url = "movie/top_rated?" +
                "language=" + tmdbLanguage +
                "&region=" + tmdbRegion +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("상영 예정 영화 목록 조회(결과 레코드 수 : {})", result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<TmdbMovieVO>>() {
        }.getType());
    }

    /**
     * Top Rated 영화 목록 조회
     *
     * @param page
     */
    public List<TmdbMovieVO> getTopRatedMovies(int page) {
        String url = "movie/top_rated?" +
                "language=" + tmdbLanguage +
                "&region=" + tmdbRegion +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("인기 영화 목록 조회(페이지 : {}, 결과 레코드 수 : {})", page, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<TmdbMovieVO>>() {
        }.getType());
    }

    /**
     * 인기 영화 목록 조회
     *
     * @param page
     */
    public List<TmdbMovieVO> getPopularMovies(int page) {
        String url = "movie/popular?" +
                "language=" + tmdbLanguage +
                "&region=" + tmdbRegion +
                "&page=" + page;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("인기 영화 목록 조회(페이지 : {}, 결과 레코드 수 : {})", page, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<TmdbMovieVO>>() {
        }.getType());
    }

    /**
     * 상영 예정 영화 목록 조회
     *
     * @param page
     */
    public List<TmdbMovieVO> getUpcomingMovies(int page) {
        String url = "movie/upcoming?" +
                "language=" + tmdbLanguage +
                "&page=" + page;
        JsonObject response = parse(sendRequest(url));

        JsonObject dates = response.get("dates").getAsJsonObject();
        String maximum = dates.get("maximum").getAsString();
        String minimum = dates.get("minimum").getAsString();
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("상영 예정 영화 목록 조회(기간 : {} - {}, 결과 레코드 수 : {})", minimum, maximum, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<TmdbMovieVO>>() {
        }.getType());
    }

    /**
     * 영화 제목으로 검색
     * String primary_release_year은 제외
     *
     * @param query
     * @param includeAdult
     * @param page
     * @param year
     */
    public List<TmdbMovieVO> getMoviesByTitle(String query, Boolean includeAdult, Integer page, String year) {
        String url = "search/movie?query=" + query + "&language=" + tmdbLanguage + "&region=" + tmdbRegion;
        if (includeAdult != null) url += "&include_adult=" + includeAdult;
        if (page != null) url += "&page=" + page;
        if (year != null) url += "&year=" + year;

        JsonObject response = parse(sendRequest(url));
        JsonArray result = response.get("results").getAsJsonArray();

        log.info("영화 제목 검색(키워드 : {}, 결과 레코드 수 : {})", query, result.size());

        return gson.fromJson(result.toString(), new TypeToken<ArrayList<TmdbMovieVO>>() {
        }.getType());
    }
}
