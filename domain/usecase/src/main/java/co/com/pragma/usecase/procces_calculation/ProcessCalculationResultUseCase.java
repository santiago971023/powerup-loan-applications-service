package co.com.pragma.usecase.procces_calculation;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.usecase.UpdateApplicationStatusUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ProcessCalculationResultUseCase {

    private final static Logger LOGGER = Logger.getLogger(ProcessCalculationResultUseCase.class.getName());

    private final UpdateApplicationStatusUseCase updateApplicationStatusUseCase;

    public Mono<Void> processResult(CalculationResultMessage resultMessage) {
        return updateApplicationStatusUseCase.updateApplicationStatus(
                resultMessage.getNewStatus().name(),
                resultMessage.getApplicationId())
                .then();
    }

}
