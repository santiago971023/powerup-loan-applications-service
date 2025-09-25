package co.com.pragma.usecase;


import co.com.pragma.model.exception.InvalidNewStatusException;
import co.com.pragma.model.exception.LoanApplicationNotFoundException;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.notification.Notification;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateApplicationStatusUseCase {

    private final static Logger LOGGER = Logger.getLogger(UpdateApplicationStatusUseCase.class.getName());


    private final LoanApplicationRepository loanApplicationRepository;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final UserRepository userRepository;

    public Mono<LoanApplication> updateApplicationStatus(String newStatus, Long loanAppId) {

        LOGGER.info("<== Se inicia método para actualizar estado de una solicitud ==>");

        if(!isValidNewStatus(newStatus)) {
            LOGGER.info("<== Confirmando si el nuevo estado es válido ==>");
            return Mono.error(new InvalidNewStatusException("El nuevo estado es inválido:" + newStatus));
        }

        LOGGER.info("<== Nuevo estado es válido ==>");
        ApplicationStatus newApplicationStatus = ApplicationStatus.valueOf(newStatus.toUpperCase());

        return loanApplicationRepository.findById(loanAppId)

                .switchIfEmpty(Mono.error(
                        new LoanApplicationNotFoundException("No sé encontró una solicitud con el id indicado.")))
                .flatMap(loanApplication -> {
                    LOGGER.info("<== Estado válido, y solicitud encontrada, se procede a guardar el nuevo estado. ==>");
                    loanApplication.setStatus(newApplicationStatus);
                    return loanApplicationRepository.save(loanApplication);
                })
                .flatMap(updatedApplication -> {
                    LOGGER.info("<== Solicitud actualizada, procediento a notificar ==>");
                    return userRepository.findUserById(updatedApplication.getUserId())
                            .flatMap(user -> {
                                Notification notification = Notification.builder()
                                        .userEmail(user.getEmail())
                                        .userName(user.getName() + " " + user.getLastname())
                                        .loanAppId(updatedApplication.getId())
                                        .newStatus(updatedApplication.getStatus().name())
                                        .loanAmount(updatedApplication.getLoanAmount())
                                        .build();
                                LOGGER.info( "<== === === Se procede a notificar === === ==>");
                                return sendNotificationUseCase.sendStatusChangeNotification(notification)
                                        .then(Mono.just(updatedApplication));
                            });
                });
    }



    private boolean isValidNewStatus(String newStatus) {

        if(newStatus == null) {
            return false;
        }
        try{
            ApplicationStatus.valueOf(newStatus.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
