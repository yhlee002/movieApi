package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Slf4j
@Service
public class PhoneMessageService {

    // sign-up 시에 핸드폰 번호 인증 메세지 전송
    public String sendMessageForSignUp(String phone) {
        Random ran = new Random();
        int authNum = ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성

        String api_key = "NCSEUE2ARTGQJVO8";
        String api_secret = "YRAN4R2GHMXBZ8VXYYTT61XDTJVNDZJZ";

        Message coolsms = new Message(api_key, api_secret);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", "01033955304");    // 수신번호
        params.put("from", phone);    // 발신번호
        params.put("type", "SMS");
        params.put("text", "This message is sended from [SiteName]. Your Verification PIN is " + authNum);
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            log.info("result : " + obj.toString());
            log.info("success_count : "+obj.get("success_count")); // 메시지아이디
        } catch (CoolsmsException e) {
            log.info("error message : "+e.getMessage());
            log.info("error code : "+e.getCode());
        }
        return Integer.toString(authNum);
    }

    // 이메일 분실시 인증 메세지 전송
    public void sendMessageForFindEmail(Member member) {

    }
}
