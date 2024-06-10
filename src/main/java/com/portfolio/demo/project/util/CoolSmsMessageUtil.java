package com.portfolio.demo.project.util;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.util.ResourceBundle;

@Slf4j
public class CoolSmsMessageUtil {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String API_KEY = resourceBundle.getString("coolSmsKey");
    private final static String API_SECRET = resourceBundle.getString("coolSmsSecret");
    private final static String sender = resourceBundle.getString("messageSender");

    // 회원가입 또는 이메일 찾기시에 핸드폰 번호 인증 메세지 전송
    public static String sendCertificationMessage(String tempKey, String phone) {
        send(phone, "Movie Site 인증번호입니다. - " + tempKey);
        log.info("문자 메세지로 전송된 인증번호 : {}", tempKey);
        return tempKey;
    }

    // 메세지 전송
    protected static void send(String to, String text) { // params에 메세지 정보를 만들어서 전달
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, "https://api.coolsms.co.kr");

        Message message = new Message();
        message.setFrom(sender);
        message.setTo(to);
        message.setText(text);

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
