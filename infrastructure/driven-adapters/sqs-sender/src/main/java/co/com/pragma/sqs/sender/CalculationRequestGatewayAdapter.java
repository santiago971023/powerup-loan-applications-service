package co.com.pragma.sqs.sender;

import co.com.pragma.model.CalculationRequestData;
import co.com.pragma.model.loanapplication.gateways.CalculationRequestGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class CalculationRequestGatewayAdapter implements CalculationRequestGateway {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queues().get("calculate-capacity"))
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> requestCalculation(CalculationRequestData data) {
        return Mono.fromCallable(() -> {
            try {
                String messageBody = objectMapper.writeValueAsString(data);

                SendMessageRequest request = SendMessageRequest.builder()
                        .queueUrl(properties.queues().get("calculate-capacity"))
                        .messageBody(messageBody)
                        .build();

                return client.sendMessage(request);
            } catch (Exception e) {
                throw new RuntimeException("Error serializando CalculationReuestData", e);
            }
        }).then();
    }
}
