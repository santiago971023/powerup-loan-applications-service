package co.com.pragma.api.config;

import co.com.pragma.api.dto.ErrorDetailDto;
import co.com.pragma.api.dto.ErrorResponseDto;
import co.com.pragma.model.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {


    private final Map<Class<? extends Throwable>, BiFunction<Throwable, ServerRequest, Mono<ServerResponse>>> exceptionHandlers;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.exceptionHandlers = Map.of(
                ConstraintViolationException.class, this::handleValidationException,
                UserNotFoundException.class, this::handleUserNotFound,
                LoanProductNotFoundException.class, this::handleLoanProductNotFound,
                LoanApplicationNotFoundException.class, this::handleLoanAppNotFound,
                InvalidNewStatusException.class, this::handleInvalidNewStatus,
                BusinessException.class, this::handleGenericError
        );
        this.setMessageWriters(configurer.getWriters());
    }


        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(
                    request -> !request.path().startsWith("/swagger-ui")
                            && !request.path().startsWith("/v3/api-docs")
                            && !request.path().startsWith("/v3/api-docs.yaml")
                            && !request.path().startsWith("/webjars"),
                    this::renderErrorResponse
            );
        }



        private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
            Throwable error = getError(request);
            log.info("Manejador global de errores interceptó: {}", error.getClass().getName());

            return exceptionHandlers.getOrDefault(error.getClass(), this::handleGenericError)
                    .apply(error, request);
        }

    private Mono<ServerResponse> handleValidationException(Throwable error, ServerRequest request) {

        ConstraintViolationException ex = (ConstraintViolationException) error;

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Error de validación en los datos de entrada.";
        String errorCode = "400_01";
        List<ErrorDetailDto> errorDetails = ex.getConstraintViolations().stream()
                    .map(violation -> new ErrorDetailDto(
                            ((PathImpl) violation.getPropertyPath()).getLeafNode().getName(),
                            violation.getMessage()
                    ))
                    .collect(Collectors.toList());
        log.warn("Error de validación {}", errorDetails);

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(finalResponse));
    }




    private Mono<ServerResponse> handleUserNotFound(Throwable error, ServerRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "No fue posible encontrar un usuario.";
        String errorCode = "404_01";
        List<ErrorDetailDto> errorDetails = List.of(new ErrorDetailDto("userNotFound", error.getMessage()));

        log.warn("Usuario no encontrado. {}: {}", request.path(), error.getMessage());

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status).bodyValue(finalResponse);
    }

    private Mono<ServerResponse> handleLoanAppNotFound(Throwable error, ServerRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "No fue posible encontrar una solicitud con el ID indicado.";
        String errorCode = "404_03";
        List<ErrorDetailDto> errorDetails = List.of(new ErrorDetailDto("LoanAppId", error.getMessage()));

        log.warn("Solicitud de préstamo no encontrada. {}: {}", request.path(), error.getMessage());

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status).bodyValue(finalResponse);
    }

    private Mono<ServerResponse> handleInvalidNewStatus(Throwable error, ServerRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "El estado que indica no es válido.";
        String errorCode = "400_01";
        List<ErrorDetailDto> errorDetails = List.of(new ErrorDetailDto("status", error.getMessage()));

        log.warn("Estado indicado no es válido. {}: {}", request.path(), error.getMessage());

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status).bodyValue(finalResponse);
    }


    private Mono<ServerResponse> handleLoanProductNotFound(Throwable error, ServerRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "No fue posible encontrar un tipo de préstamo.";
        String errorCode = "404_02";
        List<ErrorDetailDto> errorDetails = List.of(new ErrorDetailDto("LoanProductNotFound", error.getMessage()));

        log.warn("Tipo de préstamo no encontrado. {}: {}", request.path(), error.getMessage());

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status).bodyValue(finalResponse);
    }

    private Mono<ServerResponse> handleGenericError(Throwable error, ServerRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Petición inválida debido a una regla de negocio.";
        String errorCode = "400_99";

        List<ErrorDetailDto> errorDetails = List.of(new ErrorDetailDto("businessRule", error.getMessage()));
        log.warn("Error de negocio genérico: {}", error.getMessage());

        ErrorResponseDto finalResponse = ErrorResponseDto.builder()
                .errors(errorDetails)
                .message(message)
                .code(errorCode)
                .build();

        return ServerResponse.status(status).bodyValue(finalResponse);
    }


}
