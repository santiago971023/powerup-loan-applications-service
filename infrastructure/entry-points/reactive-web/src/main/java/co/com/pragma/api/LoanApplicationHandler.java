package co.com.pragma.api;

import co.com.pragma.api.dto.LoanApplicationDetailResponseDto;
import co.com.pragma.api.dto.LoanApplicationRequestDto;
import co.com.pragma.api.dto.LoanApplicationResponseDto;
import co.com.pragma.api.dto.UpdateLoanAppStatusDto;
import co.com.pragma.api.mappers.LoanApplicationDtoMapper;
import co.com.pragma.model.LoanApplicationDetail;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.pageable.DomainPageable;
import co.com.pragma.usecase.saveloanapplication.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationHandler {

    private final SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    private final ListApplicationsUseCase listApplicationsUseCase;
    private final UpdateApplicationStatusUseCase updateApplicationStatusUseCase;
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

    public Mono<ServerResponse> listApplications(ServerRequest serverRequest) {
        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);

        DomainPageable domainPageable = DomainPageable.builder()
                .pageNumber(page)
                .pageSize(size)
                .build();

        List<String> statusesStr = serverRequest.queryParam("status")
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(List.of("PENDING_REVIEW", "REJECTED", "MANUAL_REVIEW", "APPROVED"));

        List<ApplicationStatus> statuses = statusesStr.stream()
                .map(String::toUpperCase)
                .map(ApplicationStatus::valueOf)
                .collect(Collectors.toList());

        log.info("==> Petición recibida para listar solicitudes. Estados: {}, Página: {}", statuses, domainPageable);

        Flux<LoanApplicationDetail> detailFlux = listApplicationsUseCase.listApplicationsByStatus(statuses, domainPageable);

        Flux<LoanApplicationDetailResponseDto> responseDtoFlux = detailFlux
                .map(loanApplicationMapper::toDetailResponseDto);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDtoFlux, LoanApplicationDetailResponseDto.class);
    }

    public Mono<ServerResponse> updateLoanApplicationStatus(ServerRequest serverRequest) {
        Long loanAppId = Long.valueOf(serverRequest.pathVariable("loanAppId"));

        log.info("==> Petición recibida para modificar estado de solicitud. <==");

        return serverRequest.bodyToMono(UpdateLoanAppStatusDto.class)
                .flatMap(dto -> updateApplicationStatusUseCase.updateApplicationStatus(dto.getStatus(), loanAppId))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
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
