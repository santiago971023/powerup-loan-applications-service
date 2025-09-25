package co.com.pragma.sqs.listener.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "entrypoint.sqs")
public record SQSProperties(
        String region,
        String endpoint,
        String queueUrl,
        int waitTimeSeconds,
        int visibilityTimeoutSeconds,
        int maxNumberOfMessages,
        int numberOfThreads) {

    @PostConstruct
    public void logProperties() {
        System.out.println("⚡ === === === === === === === === === ===");
        System.out.println("⚡ SQSProperties loaded:");
        System.out.println("region = " + region);
        System.out.println("queueUrl = " + queueUrl);
    }
}
