package com.portfolio.demo.project.util;

import com.portfolio.demo.project.dto.social.SocialLoginParam;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import dev.akkinoc.util.YamlResourceBundle;
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
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
public class NaverLoginApiUtil {

    private static final ResourceBundle properties = YamlResourceBundle.getBundle("application", YamlResourceBundle.Control.INSTANCE);
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String CLIENTID = resourceBundle.getString("naverClientId");
    private final static String CLIENTSECRET = resourceBundle.getString("naverClientSecret");
    protected static String HOST = "";
    protected static Integer PORT = (Integer) properties.getObject("server.port");
    protected static String REDIRECT_URI = "";

    {
        String profile = System.getProperty("spring.profiles.active");
        if ("prod".equals(profile)) {
            HOST = properties.getString("public.host");
        } else {
            HOST = "localhost";
        }
        
        REDIRECT_URI = "http://" + HOST + ":" + PORT + "/api/login/oauth2/code/kakao";
    }

    public SocialLoginParam getAuthorizeData() {
        SecureRandom random = new SecureRandom();

        String callbackUrl = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8); // "http://" + HOST + "/oauth-callback/naver"
        String apiUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        String state = new BigInteger(130, random).toString();

        apiUrl += String.format("&client_id=%s&redirect_uri=%s&state=%s", CLIENTID, callbackUrl, state);

        return SocialLoginParam.builder()
                .provider(SocialLoginProvider.naver)
                .state(state)
                .apiUrl(apiUrl)
                .build();
    }

    public Map<String, String> getTokens(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = REDIRECT_URI; // "http://" + HOST + "/oauth-callback/naver";

        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
        apiURL += "client_id=" + CLIENTID;
        apiURL += "&client_secret=" + CLIENTSECRET;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + code;
        apiURL += "&state=" + state;

        log.info("Naver oauth 소셜 로그인 인증 URL: {}", apiURL);

        HttpURLConnection con = null;
        String res = "";
        Map<String, String> tokens = new HashMap<>();

        try {
            con = connect(apiURL);

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                res = readBody(con.getInputStream());
            } else { // 에러 발생
                res = readBody(con.getErrorStream());
            }

            log.info("Naver oauth 소셜 로그인 인증 결과: " + res);

            Map<String, Object> parsedJson = new JSONParser(res).parseObject();
            if (responseCode == 200) {
                String access_token = (String) parsedJson.get("access_token");
                String refresh_token = (String) parsedJson.get("refresh_token");

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
            } else if (responseCode == 400) {
                String error = (String) parsedJson.get("error");
                String errorDesc = (String) parsedJson.get("error_description");
                String errorCode = (String) parsedJson.get("error_code");

                log.error("Naver oauth 소셜 로그인 API 토큰 요청에 실패하였습니다.(errorCode: {}, errDesc: {}", errorCode, errorDesc);
                throw new IllegalStateException("Kakao oauth 소셜 로그인 API 토큰 요청에 실패하였습니다.");
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
