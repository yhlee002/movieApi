package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.dto.social.SocialProfileParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NaverProfileApiUtil {

    private static String apiURL = "https://openapi.naver.com/v1/nid/me";
    private Map<String, String> profiles = new HashMap<>();

    public SocialProfileParam getProfile(String token) throws ParseException { // 네이버 로그인 접근 토큰
        String header = "Bearer " + token; // Bearer 다음에 공백 추가

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);
        String res = get(apiURL, requestHeaders);
        log.info("네이버 프로필 조회 api 실행 결과 : "+res);

        Gson gson = new Gson();
        JsonObject parsedJson = gson.fromJson(res, JsonObject.class);
        String resultCode = parsedJson.get("resultcode").getAsString();
        String message = parsedJson.get("message").getAsString();
        JsonObject response = parsedJson.get("response").getAsJsonObject();
        /* responseBody로부터 데이터 뽑아내기*/
        SocialProfileParam profile = null;
        if(resultCode.equals("00") && message.equals("success")){
            profile = SocialProfileParam.builder()
                    .id(response.get("id").getAsString())
                    .provider(SocialLoginProvider.NAVER)
                    .name(response.get("nickname").getAsString())
                    .profileImageUrl(response.get("profile_image").getAsString())
                    .build();
        }

        return profile;
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
