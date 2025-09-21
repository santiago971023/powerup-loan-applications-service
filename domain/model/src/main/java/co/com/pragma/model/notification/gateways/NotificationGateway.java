package co.com.pragma.model.notification.gateways;

import co.com.pragma.model.notification.Notification;
import reactor.core.publisher.Mono;

public interface NotificationGateway {

    Mono<Void> sendNotification(Notification notification);

}
