package com.portfolio.demo.project.service;

import com.portfolio.demo.project.util.VonageMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

@Slf4j
@Service
public class PhoneMessageService {

    @Autowired
    VonageMessageUtil messageUtil;

    // 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송(결과 반환) + 인증키 서버로 다시 보내기
    public Map<String, String> sendCertificationMessage(String phone) {
        Map<String, String> resultMap = new HashMap<>();
        String certKey = Integer.toString(getTempKey());
        String result = messageUtil.sendPinNumber(certKey, phone);

        resultMap.put("certKey", certKey);
        resultMap.put("result", result);

        return resultMap;
    }

    // 랜덤 키 생성
    private int getTempKey() {
        Random ran = new Random();
        return ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성
    }
}
