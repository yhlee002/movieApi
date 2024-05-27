package com.portfolio.demo.project.util;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.http.*; // urlconnection.UrlConnectionHttpClient

import java.time.Duration;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AwsSmsUtil {
    public static Boolean sendMessage(String certKey, String phoneNumber) {

        final String usage = """

                Usage:    <message> <phoneNumber>

                Where:
                   message - The message text to send.
                   phoneNumber - The mobile phone number to which a message is sent (for example, +1XXX5550100).\s
                """;
        String message = "[MovieSite] 인증번호는 [" + certKey + "]입니다.";

//        if (args.length != 2) {
//            System.out.println(usage);
//            System.exit(1);
//        }
//
//        String message = args[0];
//        String phoneNumber = args[1];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .httpClientBuilder(ApacheHttpClient.builder()
                        .maxConnections(100)
                        .connectionTimeout(Duration.ofSeconds(5)))
                .build();

        Boolean result = pubTextSMS(snsClient, message, phoneNumber);
        snsClient.close();

        return result;
    }

    public static Boolean pubTextSMS(SnsClient snsClient, String message, String phoneNumber) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

            if (result.sdkHttpResponse().statusCode() == 200) return Boolean.TRUE;

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return Boolean.FALSE;
    }
}


