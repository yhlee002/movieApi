package com.portfolio.demo.project.util;

import com.portfolio.demo.project.dto.social.SocialLoginParam;
import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import dev.akkinoc.util.YamlResourceBundle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import jakarta.servlet.http.HttpServletRequest;
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
public class KakaoLoginApiUtil {

    private static ResourceBundle properties = YamlResourceBundle.getBundle("application");
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");

    protected static final String HOST = properties.getString("public.host");
    protected static final String PORT = properties.getString("server.port");

    private static final String CLIENT_ID = resourceBundle.getString("kakaoClientId");
    private static final String CLIENT_SECRET = resourceBundle.getString("kakaoClientSecret");

    Map<String, String> tokens;

    public SocialLoginParam getAuthorizeData() throws UnsupportedEncodingException {
        SecureRandom random = new SecureRandom();

        String callbackUrl = URLEncoder.encode("http://" + HOST + ":" + PORT + "/api/member/oauth2/kakao", StandardCharsets.UTF_8);
        String apiUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code";
        String state = new BigInteger(130, random).toString();

        apiUrl += String.format("&client_id=%s&redirect_uri=%s&state=%s", CLIENT_ID, callbackUrl, state);

        return SocialLoginParam.builder()
                .provider(SocialLoginProvider.KAKAO)
                .state(state)
                .apiUrl(apiUrl)
                .build();
    }

    public Map<String, String> getTokens(HttpServletRequest request) throws UnsupportedEncodingException {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode("http://localhost:8077/api/member/oauth2/kakao", "UTF-8");

        String apiURL = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code";
        apiURL += "&client_id=" + CLIENT_ID;
        apiURL += "&client_secret=" + CLIENT_SECRET;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + code;
//        apiURL += "&state=" + state;

        log.info("Kakao oauth 소셜 로그인 인증 URL: {}", apiURL);

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

            log.info("Kakao oauth 소셜 로그인 인증 결과: " + res);

            if (responseCode == 200) {
                Map<String, Object> parsedJson = new JSONParser(res).parseObject();

                String access_token = (String) parsedJson.get("access_token");
                String refresh_token = (String) parsedJson.get("refresh_token");

                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException("Kakao oauth 소셜 로그인 API 토큰 요청에 실패하였습니다.", e);
        } finally {
            con.disconnect();
        }
        return tokens;
    }

    private static String readBody(InputStream stream) {
        InputStreamReader streamReader = new InputStreamReader(stream);

        try(BufferedReader br = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line = null;
            if ((line = br.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
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

