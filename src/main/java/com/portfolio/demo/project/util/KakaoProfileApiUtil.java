package com.portfolio.demo.project.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.dto.social.SocialProfileParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class KakaoProfileApiUtil {
    private static String URL = "https://kapi.kakao.com/v2/user/me";
    private SocialProfileParam profile;

    public SocialProfileParam getProfile(String token) throws ParseException {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Authorization", "Bearer " + token);
        String res = get(URL, requestHeader);

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(res, JsonObject.class);
        JsonObject accountObj = obj.get("kakao_account").getAsJsonObject();
        JsonObject profileObj = accountObj.get("profile").getAsJsonObject();

        profile = SocialProfileParam.builder()
                .id(obj.get("id").getAsString())
                .provider(SocialLoginProvider.KAKAO)
                .name(profileObj.get("nickname").getAsString())
                .thumbnailImageUrl(profileObj.get("thumbnail_image_url").getAsString())
                .profileImageUrl(profileObj.get("profile_image_url").getAsString())
                .build();

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
            throw new RuntimeException("Kakao 프로필 API 요청에 실패하였습니다.", e);
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
            throw new RuntimeException("KaKao 프로필 API의 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static HttpURLConnection connect(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Kakao 프로필 API의 URL이 잘못되었습니다. : " + urlStr, e);
        } catch (IOException e) {
            throw new RuntimeException("Kakao 프로필 API와의 연결이 실패했습니다. : " + urlStr, e);
        }
    }
}
