package co.com.pragma.sqs.sender;

import co.com.pragma.model.notification.Notification;
import co.com.pragma.model.notification.gateways.NotificationGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import org.reactivecommons.utils.ObjectMapper;
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
public class NotificationGatewayAdapter implements NotificationGateway {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper mapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> sendNotification(Notification notification) {
        log.info("<== Enviando notificación para la solicitud ID: {}", notification.getLoanAppId());

        return Mono.fromCallable( () -> {
            return mapper.map(notification, String.class);
        })
                .flatMap(messageBody -> {
                    SendMessageRequest request = SendMessageRequest.builder()
                            .queueUrl(properties.queueUrl())
                            .messageBody(messageBody)
                            .build();

                    return Mono.fromFuture(client.sendMessage(request))
                            .doOnSuccess(response -> log.info("Mensaje de notificación enviado con éxito {}", response.messageId()))
                            .doOnError( e -> log.error("Error al enviar el mensaje de notificación s SQS ",  e))
                            .then();
                });

    }
}
