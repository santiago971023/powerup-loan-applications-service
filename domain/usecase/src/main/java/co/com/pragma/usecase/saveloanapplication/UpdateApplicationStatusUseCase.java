package co.com.pragma.usecase.saveloanapplication;


import co.com.pragma.model.exception.InvalidNewStatusException;
import co.com.pragma.model.exception.LoanApplicationNotFoundException;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.gateways.NotificationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateApplicationStatusUseCase {

    private final static Logger LOGGER = Logger.getLogger(UpdateApplicationStatusUseCase.class.getName());


    private final LoanApplicationRepository loanApplicationRepository;

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
