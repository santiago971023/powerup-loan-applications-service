package co.com.pragma.consumer;

import co.com.pragma.model.exception.UserNotFoundException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
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
                        clientResponse -> Mono.error(new UserNotFoundException("Usuario no encontrado"))
                )
                .bodyToMono(User.class)
                .onErrorResume(UserNotFoundException.class, e -> Mono.empty());
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return client.get()
                .uri("/api/v1/users/id/{id}", id)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.empty()
                )
                .bodyToMono(UserClientResponseDto.class)
                .map(dto -> {
                    User user = new User();
                    user.setId(dto.getId());
                    user.setName(dto.getName());
                    user.setLastname(dto.getLastname());
                    user.setEmail(dto.getEmail());
                    user.setSalary(dto.getUserSalary());
                    log.info("=== === === === === === === === === === === === ===");
                    log.info(user.toString());
                    return user;

                });
    }
}
