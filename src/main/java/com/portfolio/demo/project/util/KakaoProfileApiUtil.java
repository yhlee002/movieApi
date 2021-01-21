package com.portfolio.demo.project.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class KakaoProfileApiUtil {
    private static String URL = "https://kapi.kakao.com/v2/user/me";
    private Map<String, String> profiles = new HashMap<>();

    public Map<String, String> getProfile(String token) throws ParseException {
        String header = "Bearer "+token;

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Authorization", header);
        String res = get(URL, requestHeader);

        Map<String, Object> parsedJson = new JSONParser(res).parseObject();
        String id = String.valueOf(parsedJson.get("id"));
        Map<String, Object> kakao_account = (Map<String, Object>) parsedJson.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
        String nickname = (String) profile.get("nickname");
        String profile_image = (String) profile.get("profile_image_url");

        profiles.put("id", id.toString());
        profiles.put("nickname", nickname);
        profiles.put("profile_image", profile_image);

        return profiles;
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
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + urlStr, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + urlStr, e);
        }
    }
}
