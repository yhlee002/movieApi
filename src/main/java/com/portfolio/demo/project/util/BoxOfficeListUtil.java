package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.kobis.movie.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BoxOfficeListUtil {

    private final static String DAILYBOXOFFICE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private final static String WEEKLYBOXOFFICE_URL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json";
    private final static String MOVIEINFO_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    private String KEY = null;

    private final Gson gson = new Gson();

    public void setKey(String key) {
        this.KEY = key;
    }

    public List<MovieVO> getDailyBoxOfficeMovies(String targetDt) {
        List<MovieVO> movieList = null;

        try {

            HttpURLConnection con = getConnection(DAILYBOXOFFICE_URL + "?key=" + KEY + "&targetDt=" + targetDt);
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

    public List<MovieVO> getWeeklyBoxOfficeMovies(String targetDt) {
        List<MovieVO> movieList = null;

        try {
            HttpURLConnection con = getConnection(WEEKLYBOXOFFICE_URL + "?key=" + KEY + "&targetDt=" + targetDt);
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
            HttpURLConnection con = getConnection(MOVIEINFO_URL + "?key=" + KEY + "&movieCd=" + movieCd);
            String res = null;

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                res = getResult(con.getInputStream());

                JsonObject result = gson.fromJson(res, JsonObject.class);
                JsonObject movieInfoResult = result.get("movieInfoResult").getAsJsonObject();
                JsonObject movieInfo = movieInfoResult.get("movieInfo").getAsJsonObject();

                MovieDetailVO movieDetail = gson.fromJson(movieInfoResult.get("movieInfo").toString(), MovieDetailVO.class); // new TypeToken<ArrayList<MovieVO>>() {}.getType()


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
}
