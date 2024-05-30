package com.portfolio.demo.project.util;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

@Slf4j
public class CoolSmsMessageUtil {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String API_KEY = resourceBundle.getString("coolSmsKey");
    private final static String API_SECRET = resourceBundle.getString("coolSmsSecret");
    private final static String sender = resourceBundle.getString("testPhoneNum");

    // 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송
    public String sendCertificationMessage(String phone) {
        int tempKey = getTempKey();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phone);    // 수신번호
        params.put("from", sender);    // 발신번호
        params.put("type", "SMS");
        params.put("text", "Movie Info Site 인증번호입니다. - " + tempKey);

        send(params);
        log.info("문자 메세지로 전송된 인증번호 : {]", tempKey);
        return Integer.toString(tempKey);
    }

    // 랜덤 키 생성
    protected int getTempKey() {
        Random ran = new Random();
        return ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성
    }

    // 메세지 전송
    protected void send(HashMap<String, String> params) { // params에 메세지 정보를 만들어서 전달
        Message coolsms = new Message(API_KEY, API_SECRET);

        try {
            JSONObject obj = coolsms.send(params);
            log.info("result : {}", obj.toString());
            log.info("success_count : {}", obj.get("success_count"));
        } catch (CoolsmsException e) {
            log.info("error message : {}", e.getMessage());
            log.info("error code : {}", e.getCode());
        }
    }
}
