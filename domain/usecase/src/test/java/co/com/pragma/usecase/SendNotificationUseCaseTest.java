package co.com.pragma.usecase;

import co.com.pragma.model.exception.NotificationEmailNotFoundException;
import co.com.pragma.model.notification.Notification;
import co.com.pragma.model.notification.gateways.NotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationUseCaseTest {


    @Mock
    private NotificationGateway notificationGateway;

    @InjectMocks
    private SendNotificationUseCase sendNotificationUseCase;


    @Test
    @DisplayName("La notificación del cambio de status debería ser exitosa.")
    void sendStatusChangeNotificationShouldBeSuccessful() {

        Notification notification = Notification.builder()
                .userEmail("svr@test.com")
                .userName("Santiago Valencia")
                .loanAppId(123L)
                .newStatus("APPROVED")
                .loanAmount(new BigDecimal("5000"))
                .build();

        when(notificationGateway.sendNotification(notification))
                .thenReturn(Mono.empty());

        Mono<Void> result = sendNotificationUseCase.sendStatusChangeNotification(notification);

        StepVerifier.create(result)
                .verifyComplete();
    }


    @Test
    @DisplayName("Debería lanzar una excepción cuando no encuentre el email.")
    void sendStatusChangeNotificationShouldThrowAnExceptionEmailNotFound() {

        Notification notification = Notification.builder()
                .userName("Santiago Valencia")
                .loanAppId(123L)
                .newStatus("APPROVED")
                .loanAmount(new BigDecimal("5000"))
                .build();


        Mono<Void> result = sendNotificationUseCase.sendStatusChangeNotification(notification);
        // 🔹 Verificar que lanza NotificationEmailNotFoundException
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotificationEmailNotFoundException &&
                                throwable.getMessage().equals("No hemos encontrado el email del destinatario.")
                )
                .verify();
    }

}