package com.portfolio.demo.project.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AwsSmsUtil {

    public static final String AWS_ACCESS_KEY_ID = "aws.accessKeyId";
    public static final String AWS_SECRET_KEY = "aws.secretKey";

    private static final ResourceBundle properties = ResourceBundle.getBundle("Res_ko_KR_keys");
    static String awsAccessKey = properties.getString("aws.access.key");
    static String awsSecretKey = properties.getString("aws.secret.key");

    static {
        System.setProperty(AWS_ACCESS_KEY_ID, awsAccessKey);
        System.setProperty(AWS_SECRET_KEY, awsSecretKey);
    }

    public static void sendCertificationMessage(String certKey, String phone) {
        String message = "[MovieSite] 인증번호는 " + certKey + "입니다.";
        sendSingleSMS(message, phone);
    }

    static void sendSingleSMS(String message, String phone) {
        AmazonSNS snsClient = AmazonSNSClient.builder().withRegion(Regions.AP_NORTHEAST_1).build();
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID",
                new MessageAttributeValue().withStringValue("MyWebsite").withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.SMSType",
                new MessageAttributeValue().withStringValue("Transactional").withDataType("String"));

        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phone)
                .withMessageAttributes(smsAttributes));

        System.out.println("Message sent successfully--" + result.getMessageId());
    }
}


