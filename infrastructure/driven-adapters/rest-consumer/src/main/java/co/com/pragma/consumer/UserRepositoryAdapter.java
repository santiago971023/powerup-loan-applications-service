package co.com.pragma.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter /* implements Gateway from domain */{
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<UserResponseDto> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<UserResponseDto> testPost() {
        UserRequestDto request = UserRequestDto.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), UserRequestDto.class)
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }
}
