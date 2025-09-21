package co.com.pragma.usecase;


import co.com.pragma.model.exception.NotificationEmailNotFoundException;
import co.com.pragma.model.notification.Notification;
import co.com.pragma.model.notification.gateways.NotificationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class SendNotificationUseCase {

    private final static Logger LOGGER = Logger.getLogger(SendNotificationUseCase.class.getName());

    private final NotificationGateway notificationGateway;

    public Mono<Void> sendStatusChangeNotification(Notification notification) {

        LOGGER.info(" <== Iniciando caso de uso para enviar notificación de cambio de estado en una solicitud ==>");

        if(notification.getUserEmail() == null || notification.getUserEmail().isBlank()) {
            return Mono.error(new NotificationEmailNotFoundException("No hemos encontrado el email del destinatario."));
        }

        return notificationGateway.sendNotification(notification);
    }

}
