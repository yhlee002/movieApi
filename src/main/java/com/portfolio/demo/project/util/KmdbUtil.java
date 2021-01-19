//package com.portfolio.demo.project.util;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.portfolio.demo.project.vo.movieInfo.KmdbMovieDetailVO;
//import lombok.extern.slf4j.Slf4j;
//
//import javax.annotation.PostConstruct;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//@Slf4j
//public class KmdbUtil {
//    private final static String KMDB_URL = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&";
//    private ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_Keys");
//    private static String key;
//    private static int listCount = 3;
//
//    @PostConstruct
//    private void initialize() {
//        key = resourceBundle.getString("kmdbKey");
//    }
//
//    public KmdbMovieDetailVO getMovieDetail(String title, String director) {
//        String titleStr = stringReplace(title);
//        String directorStr = director.replace(" ", "");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(KMDB_URL + "ServiceKey=" + key);
//        sb.append("&title=" + titleStr.replace(" ", ""));
//        sb.append("&director=" + directorStr + "&listCount=" + listCount);
//
//
//        log.info("URL : " + sb.toString());
//        KmdbMovieDetailVO movieDetailVO = null;
//        try {
//            URL url = new URL(sb.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                String response = getResponse(conn.getInputStream());
//
//                Gson gson = new Gson();
//                JsonObject totalResult = gson.fromJson(response, JsonObject.class);
//                JsonArray result = totalResult.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("Result").getAsJsonArray(); // .getAsJsonObject().get("Result").getAsJsonArray();
//                movieDetailVO = gson.fromJson(result.get(0).toString(), KmdbMovieDetailVO.class);
//
//            } else {
//                log.info("responseCode : " + responseCode);
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return movieDetailVO;
//    }
//
//
//    private String getResponse(InputStream stream) {
//        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        try {
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return sb.toString();
//    }
//
//    private String stringReplace(String str){
//        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
//        str =str.replaceAll(match, "");
//        return str;
//    }
//}
