package co.com.pragma.usecase;

import co.com.pragma.model.exception.LoanProductNotFoundException;
import co.com.pragma.model.exception.UserNotFoundException;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanproduct.gateways.LoanProductRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class SaveLoanApplicationUseCase {

    private final static Logger LOGGER = Logger.getLogger(SaveLoanApplicationUseCase.class.getName());

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final LoanProductRepository loanProductRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input {
        private String idDocument;
        private Long loanProductId;
        private BigDecimal loanAmount;
        private Integer termInMonths;
    }

    public Mono<LoanApplication> saveLoanApplication(Input input) {
        LOGGER.info("Iniciando caso de uso para guardar solicitud de préstamo para el documento: " + input.idDocument);
        return userRepository.findUserByIdDocument(input.idDocument)
                .switchIfEmpty(Mono.error(new UserNotFoundException("No se encontró un usuario con este documento.")))
                .flatMap(user ->
                        loanProductRepository.findById(input.loanProductId)
                                .switchIfEmpty(Mono.error(new LoanProductNotFoundException("No se encontró un producto con ese id")))
                                .flatMap(loanProduct -> {
                                    LOGGER.info("Creando mi solicitud de crédito para el documento: " + input.idDocument);
                                    LoanApplication loanApp = new LoanApplication();
                                    loanApp.setLoanAmount(input.loanAmount);
                                    loanApp.setTermInMonths(input.termInMonths);
                                    loanApp.setLoanProductId(input.loanProductId);
                                    loanApp.setUserId(user.getId());
                                    loanApp.setCreationDate(LocalDateTime.now());
                                    loanApp.setStatus(ApplicationStatus.PENDING_REVIEW);
                                    return loanApplicationRepository.save(loanApp);
                                })
                );
    }


}
