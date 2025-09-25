package co.com.pragma.sqs.listener;

import co.com.pragma.usecase.procces_calculation.CalculationResultMessage;
import co.com.pragma.usecase.procces_calculation.ProcessCalculationResultUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class CalculationResultListener implements Function<Message, Mono<Void>> {

    private final ProcessCalculationResultUseCase processCalculationResultUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {

        log.info("<== <== <== <== MENSAJE RECIBIDO: " +  message + "==> ==> ==> ==>");

        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), CalculationResultMessage.class))
                .flatMap(dto -> processCalculationResultUseCase.processResult(dto))
                .then();
    }
}
