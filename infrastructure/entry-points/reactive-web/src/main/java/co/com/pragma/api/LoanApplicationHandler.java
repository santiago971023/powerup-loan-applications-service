package co.com.pragma.api;

import co.com.pragma.api.dto.LoanApplicationRequestDto;
import co.com.pragma.api.dto.LoanApplicationResponseDto;
import co.com.pragma.api.mappers.LoanApplicationDtoMapper;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.usecase.saveloanapplication.SaveLoanApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationHandler {

    private final SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    private final LoanApplicationDtoMapper loanApplicationMapper;
    private final Validator validator;



    public Mono<ServerResponse> saveLoanApplication(ServerRequest serverRequest) {
        log.info("=== === Inicio de petición recibida para guardar una solicitud de préstamo === ===");
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .doOnNext(dto -> log.info("Dto extraído del cuerpo de la petición {}", dto) )
                .flatMap(this::validateRequestDto)
                .map(loanApplicationMapper::toUseCaseInput)
                .flatMap(saveLoanApplicationUseCase::saveLoanApplication)
                .flatMap(savedLoanApplication -> {
                    LoanApplicationResponseDto responseDto = loanApplicationMapper.toResponse(savedLoanApplication);
                    log.info("<== Solicitud creada con éxito: {}", responseDto);
                    return ServerResponse.status(HttpStatus.CREATED).bodyValue(responseDto);
                });


    }

    // Metodo privados  CLASE APARTE
    private Mono<LoanApplicationRequestDto> validateRequestDto(LoanApplicationRequestDto dto) {
        Set<ConstraintViolation<LoanApplicationRequestDto>> violations = validator.validate(dto);
        if(violations.isEmpty()) {
            return Mono.just(dto);
        }
        log.warn("! VALIDACIÓN FALLIDA: Se encontraron {} violaciones en el DTO: {}", violations.size(), violations);
        return Mono.error(new ConstraintViolationException(violations));
    }
}
