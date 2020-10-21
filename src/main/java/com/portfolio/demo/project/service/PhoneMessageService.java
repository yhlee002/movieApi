package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class PhoneMessageService {
    public static final String ACCOUNT_SID = "AC386670919b29b9085024047e24813d2a";
    public static final String AUTH_TOKEN = "1635d54ab2f08dfec4aa12dcd9cc82de";

    // sign-up 시에 핸드폰 번호 인증 메세지 전송
    public String sendMessageForSignUp(String phone) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN); // username(ACCOUNT_SID), password(AUTH_TOKEN)를 입력받음
        Random ran = new Random();

        int authNum = ran.nextInt(9000) + 1000; // => 1000 ~ 9999 범위의 난수 생성

        // phoneNum(000-0000-0000)의 가장 앞자리수를 지우고 그 자리에 +82붙이기
        String phoneNum = "+82" + phone.substring(1);

        // Message.creator("to", "from", "content").create();
        Message message = Message.creator(new PhoneNumber(phoneNum), // "+821033955304"
                new PhoneNumber("+12057827269"),
                "This message is sended from SiteName. Your Verification PIN is " + authNum).create();

        log.info("message sid : "+message.getSid());

        return Integer.toString(authNum);
    }

    // 이메일 분실시 인증 메세지 전송
    public void sendMessageForFindEmail(Member member){

    }
}
