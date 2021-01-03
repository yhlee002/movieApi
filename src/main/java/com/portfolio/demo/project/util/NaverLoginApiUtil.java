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

@Slf4j
public class NaverLoginApiUtil {

    Map<String, String> tokens;

    public Map<String, String> getTokens(HttpServletRequest request) throws UnsupportedEncodingException {
        String clientId = "RxgOCy0eNX66Nbp0rWRH";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "zirmKwxjqs";//애플리케이션 클라이언트 시크릿값";
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode("http://localhost:8080/sign-in/naver/oauth2", "UTF-8");

        String apiURL;
        apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
        apiURL += "client_id=" + clientId;
        apiURL += "&client_secret=" + clientSecret;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + code;
        apiURL += "&state=" + state;
        log.info("apiURL=" + apiURL);

        HttpURLConnection con = null;
        String res = "";
        tokens = new HashMap<>();

        try {
            con = connect(apiURL);

            int responseCode = con.getResponseCode();
            log.info("responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = readBody(con.getInputStream());
            } else { // 에러 발생
                res = readBody(con.getErrorStream());
            }

            if (responseCode == 200) {
                Map<String, Object> parsedJson = new JSONParser(res).parseObject();
                System.out.println(parsedJson);

                String access_token = (String) parsedJson.get("access_token");
                String refresh_token = (String) parsedJson.get("refresh_token");
                log.info("access token : " + access_token);
                log.info("refresh token : " + refresh_token);

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
}
