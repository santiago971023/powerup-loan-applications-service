package co.com.pragma.usecase;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.gateways.CalculationRequestGateway;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ProcessCalculationResultUseCase {

    private final static Logger LOGGER = Logger.getLogger(ProcessCalculationResultUseCase.class.getName());

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final SendNotificationUseCase sendNotificationUseCase;

    // Esta clase es el resultado que envía la lambda
    public static class Input {
        private Long applicationId;
        private ApplicationStatus newStatus;
        private String reason;
    }


    public Mono<Void> processResult(Input input) {
        LOGGER.info("Iniciando caso de uso para procesar resultado del cálculo para la solicitud con ID: " + input.applicationId);
        return null;
        //return loanApplicationRepository.findById(input.applicationId);
    }



}
