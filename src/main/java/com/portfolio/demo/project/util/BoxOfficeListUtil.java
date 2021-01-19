package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.kobis.movie.MovieElementVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BoxOfficeListUtil {

    private static String MOVIELISTURL = "http://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
    private static String KEY = null;
    private static String itemPerPage = "100";
    private static String openStartDt = "2020";
    private static int curPage = 1;
    private static int totCont;


    public List<MovieElementVO> getMovieList(String KEY) {
        List<MovieElementVO> movieElementList = null;
        String res = null;
        curPage++;

        String apiUrl = MOVIELISTURL + "?key=" + KEY + "&itemPerPage=" + itemPerPage + "&openStartDt=" + openStartDt + "&curPage=" + curPage;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                res = getResult(con.getInputStream());

                Gson gson = new Gson();
                JsonObject result = gson.fromJson(res, JsonObject.class);
                JsonObject movieListResult = result.get("movieListResult").getAsJsonObject();
                totCont = movieListResult.get("totCnt").getAsInt();
                JsonArray movieList = movieListResult.get("movieList").getAsJsonArray();
                movieElementList = gson.fromJson(movieList.toString(), new TypeToken<ArrayList<MovieElementVO>>() {
                }.getType());

            } else {
                res = getResult(con.getErrorStream());
                log.info(res);
                con.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieElementList;
    }

    public HttpURLConnection connect(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
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
