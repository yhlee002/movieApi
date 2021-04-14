package com.portfolio.demo.project.util;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

@Slf4j
public class VonageMessageUtil {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
    private final static String API_KEY = resourceBundle.getString("vonageMessageKey");
    private final static String API_SECRET = resourceBundle.getString("vonageMessageSecret");
    private final static String sender = resourceBundle.getString("testPhoneNum");

    private static VonageClient client = VonageClient.builder().apiKey(API_KEY).apiSecret(API_SECRET).build();

    public String sendPinNumber(String certKey, String phone) {
        TextMessage message = new TextMessage("Vonage APIs",
                sender,
                "Movie Info Site 인증번호입니다. - " + certKey);

        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        String result = "";
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            log.info("[Vonage] Message sent successfully.");
            result = "success";
        } else {
            log.info("[Vonage] Message failed with error: " + response.getMessages().get(0).getErrorText());
            result = "fail";
        }
        return result;
    }
}
