package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolio.demo.project.vo.naver.NaverMovieDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
public class NaverMovieInfoUtil {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String CLIENTID = resourceBundle.getString("naverClientId");
    private final static String CLIENTSECRET = resourceBundle.getString("naverClientSecret");

    public List<NaverMovieDetailVO> getMovieListByTitle(String title) {
        String titleEncoded = null;
        List<NaverMovieDetailVO> movieList = null;
        try {
            titleEncoded = URLEncoder.encode(title, "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/search/movie?query=" + titleEncoded;
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", CLIENTID);
            requestHeaders.put("X-Naver-Client-Secret", CLIENTSECRET);

            log.info(title + " 이름에 대한 api 주소 : " + apiURL);
            String json = get(apiURL, requestHeaders);


            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONArray items = (JSONArray) obj.get("items");

            Gson gson = new Gson();
            movieList = gson.fromJson(items.toString(), new TypeToken<ArrayList<NaverMovieDetailVO>>() {
            }.getType());
            for (NaverMovieDetailVO vo : movieList) {
                vo.setDirector(vo.getDirector().replace("|", " "));

                if (vo.getActor().length() != 0) {
                    String actor = vo.getActor().replace("|", ", ");
                    vo.setActor(actor.substring(0, actor.lastIndexOf(", ")));
                }
            }

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return movieList;
    }

    public String getMovieThumnailImg(String movieNm) {
        String imgUrl = null;
        try {
            String text = URLEncoder.encode(movieNm, "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/search/movie?query=" + text;
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", CLIENTID);
            requestHeaders.put("X-Naver-Client-Secret", CLIENTSECRET);

            String json = get(apiURL, requestHeaders);
            log.info(movieNm + " 이름에 대한 api 주소 : " + apiURL);
            log.info("결과 : " + json);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);

            JSONArray items = (JSONArray) obj.get("items");
            JSONObject item = (JSONObject) items.get(0);

            imgUrl = item.get("image").toString(); // (String)

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
