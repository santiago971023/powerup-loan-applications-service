package co.com.pragma.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> loanApplicationRoutes(LoanApplicationHandler loanApplicationHandler) {
        return route(
                POST("/api/v1/loan-app").and(accept(MediaType.APPLICATION_JSON)),
                loanApplicationHandler::saveLoanApplication
        ).andRoute(GET("/api/v1/loan-app").and(accept(MediaType.APPLICATION_JSON)),
                loanApplicationHandler::listApplications);

    }
}
