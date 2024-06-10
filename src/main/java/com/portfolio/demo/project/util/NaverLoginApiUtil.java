package com.portfolio.demo.project.util;

import com.portfolio.demo.project.dto.SocialLoginParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
public class NaverLoginApiUtil {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");

    private final static String CLIENTID = resourceBundle.getString("naverClientId");
    private final static String CLIENTSECRET = resourceBundle.getString("naverClientSecret");

    Map<String, String> tokens;

    public SocialLoginParam getAuthorizeData() throws UnsupportedEncodingException {
        SecureRandom random = new SecureRandom();

        String callbackUrl = URLEncoder.encode("http://localhost:8077/api/member/oauth2/naver", "utf-8");
        String apiUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        String state = new BigInteger(130, random).toString();

        apiUrl += String.format("&client_id=%s&redirect_uri=%s&state=%s", CLIENTID, callbackUrl, state);

        return SocialLoginParam.builder()
                .provider("naver")
                .state(state)
                .apiUrl(apiUrl)
                .build();
    }

    public Map<String, String> getTokens(HttpServletRequest request) throws UnsupportedEncodingException {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode("http://localhost:8077/api/member/oauth2/naver", "UTF-8");

        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
        apiURL += "client_id=" + CLIENTID;
        apiURL += "&client_secret=" + CLIENTSECRET;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + code;
        apiURL += "&state=" + state;

        log.info("Naver oauth 소셜 로그인 인증 URL: {}", apiURL);

        HttpURLConnection con = null;
        String res = "";
        tokens = new HashMap<>();

        try {
            con = connect(apiURL);

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = readBody(con.getInputStream());
            } else { // 에러 발생
                res = readBody(con.getErrorStream());
            }

            log.info("Naver oauth 소셜 로그인 인증 결과: " + res);

            if (responseCode == 200) {
                Map<String, Object> parsedJson = new JSONParser(res).parseObject();
                log.info(parsedJson.toString());

                String access_token = (String) parsedJson.get("access_token");
                String refresh_token = (String) parsedJson.get("refresh_token");

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Naver oauth 소셜 로그인 API 토큰 요청에 실패하였습니다.", e);
        } finally {
            if (con != null) con.disconnect();
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
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            return conn;
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
}
