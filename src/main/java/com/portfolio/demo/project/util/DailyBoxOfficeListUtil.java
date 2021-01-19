package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.kobis.movie.MovieVO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
public class DailyBoxOfficeListUtil {
    private final static String DAILYBOXOFFICE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private String targetDt = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public ArrayList<MovieVO> getMovieList(String key) {
        ArrayList<MovieVO> movieList = null;
        String apiUrl = DAILYBOXOFFICE_URL + "?key=" + key + "&targetDt=" + targetDt;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            String res = null;
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = getResult(con.getInputStream());

                Gson gson = new Gson();

                JsonObject result = gson.fromJson(res, JsonObject.class);
                JsonObject boxOfficeResult = result.get("boxOfficeResult").getAsJsonObject();
                JsonArray dailyBoxOfficeList = boxOfficeResult.get("dailyBoxOfficeList").getAsJsonArray();
                movieList = gson.fromJson(dailyBoxOfficeList.toString(), new TypeToken<ArrayList<MovieVO>>() {}.getType());

            } else { // 에러 발생
                res = getResult(con.getErrorStream());
                log.error(res);
                con.disconnect();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
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
