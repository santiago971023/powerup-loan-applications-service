package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findUserByIdDocument(String idDocument);
    Mono<User> findUserById(Long id);
}
