package com.portfolio.demo.project.util;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.ResourceBundle;

@Slf4j
public class VonageMessageUtil {
    private static Base64.Encoder encoder = Base64.getEncoder();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String APP_ID = resourceBundle.getString("vonageAppId");
    private final static String API_KEY = resourceBundle.getString("vonageMessageKey");
    private final static String API_SECRET = resourceBundle.getString("vonageMessageSecret");
    private final static String sender = resourceBundle.getString("testPhoneNum");

    private static VonageClient client = VonageClient.builder().apiKey(API_KEY).apiSecret(API_SECRET).build();

    public String sendPinNumber(String certKey, String phone) {
        String toNumber = "82" + phone.replace("-", "").substring(1); // '000-0000-0000' 형태의 값 변환
        TextMessage message = new TextMessage(sender, toNumber, "Movie Info Site PIN number : " + certKey);
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        log.info("to number : "+toNumber);
        log.info("from number : "+sender);

        String result = "";
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            log.info("[Vonage] Message sent successfully.");
            result = "success";
        } else {
            log.info("[Vonage] Message failed with error: " + response.getMessages().get(0).getErrorText());
            log.info("[Vonage] entire response message: " + response.getMessages());
            result = "fail";
        }
        return result;
    }
}
