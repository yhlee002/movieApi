package com.portfolio.demo.project.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class KakaoLoginApiUtil {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private static String CLIENT_ID = resourceBundle.getString("kakaoClientId");
    private static String CLIENT_SECRET = resourceBundle.getString("kakaoClientSecret");

    Map<String, String> tokens;

    public Map<String, String> getTokens(HttpServletRequest request) throws UnsupportedEncodingException {

        System.out.println(request.getParameterMap().keySet().toString());
        System.out.println(request.getParameterMap().values());

        String kakaoCode = request.getParameter("code");
        String kakaoState = request.getParameter("state");
        String redirectURI = URLEncoder.encode("http://3.36.203.4:8080/sign-in/kakao/oauth2", "UTF-8");

        String apiURL = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code";
        apiURL += "&client_id=" + CLIENT_ID;
        apiURL += "&client_secret=" + CLIENT_SECRET;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + kakaoCode;

        log.info("apiURL=" + apiURL);

        HttpURLConnection con = null;
        String res = "";
        tokens = new HashMap<>();

        con = connect(apiURL);

        try {
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                res = readBody(con.getInputStream());
            } else {
                res = readBody(con.getErrorStream());
            }

            log.info("res : "+res);

            if (responseCode == 200) {
                Map<String, Object> parsedJson = new JSONParser(res).parseObject();

                String access_token = (String) parsedJson.get("access_token");
                String refresh_token = (String) parsedJson.get("refresh_token");

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
        return tokens;
    }

    private static String readBody(InputStream stream) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            if ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static HttpURLConnection connect(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            return conn;
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + urlStr, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + urlStr, e);
        }
    }
}

