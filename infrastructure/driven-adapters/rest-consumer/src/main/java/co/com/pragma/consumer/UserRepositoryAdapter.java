package co.com.pragma.consumer;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserRepositoryAdapter implements UserRepository {

    private final WebClient client;

    public UserRepositoryAdapter(@Qualifier("userServiceWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<User> findUserByIdDocument(String idDocument) {

        return client.get()
                .uri("/api/v1/users/document/{idDocument}", idDocument)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.empty()
                )
                .bodyToMono(User.class);
    }
}
