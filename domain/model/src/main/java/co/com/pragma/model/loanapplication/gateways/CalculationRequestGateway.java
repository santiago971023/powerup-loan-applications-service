package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.CalculationRequestData;
import reactor.core.publisher.Mono;

// Puerto de salida para enviar mensaje a la primera cola
public interface CalculationRequestGateway {

    Mono<Void> requestCalculation(CalculationRequestData data);

}
