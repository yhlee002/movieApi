package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.kobis.movie.DirectorVO;
import com.portfolio.demo.project.vo.kobis.movie.GenreVO;
import com.portfolio.demo.project.vo.kobis.movie.MovieDetailVO;
import com.portfolio.demo.project.vo.kobis.movie.NationVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MovieInfoUtil {

    private final static String MOVIEINFO_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    private String KEY = null;

    public void setKey(String key) {
        this.KEY = key;
    }

    public MovieDetailVO getMovieInfo(String movieCd) {
        MovieDetailVO movieDetail = null;
        String apiUrl = MOVIEINFO_URL + "?key=" + KEY + "&movieCd=" + movieCd;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            String res = null;
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                res = getResult(con.getInputStream());

                Gson gson = new Gson();

                JsonObject result = gson.fromJson(res, JsonObject.class);
                JsonObject movieInfoResult = result.get("movieInfoResult").getAsJsonObject();
                JsonObject movieInfo = movieInfoResult.get("movieInfo").getAsJsonObject();

                movieDetail = gson.fromJson(movieInfoResult.get("movieInfo").toString(), MovieDetailVO.class); // new TypeToken<ArrayList<MovieVO>>() {}.getType()

                List<NationVO> nations = gson.fromJson(movieInfo.get("nations").getAsJsonArray().toString(), new TypeToken<ArrayList<NationVO>>() {
                }.getType());
                List<GenreVO> genres = gson.fromJson(movieInfo.get("genres").toString(), new TypeToken<ArrayList<GenreVO>>() {
                }.getType());
                List<DirectorVO> directors = gson.fromJson(movieInfo.get("directors").toString(), new TypeToken<ArrayList<DirectorVO>>() {
                }.getType());

            } else { // 에러 발생
                res = getResult(con.getErrorStream());
                log.error(res);
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieDetail;
    }

    public String getResult(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        log.info(sb.toString());
        return sb.toString();
    }
}
