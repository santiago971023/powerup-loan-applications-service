package co.com.pragma.model.loanproduct.gateways;

import co.com.pragma.model.loanproduct.LoanProduct;
import reactor.core.publisher.Mono;

public interface LoanProductRepository {
    Mono<LoanProduct> findById(Long id);
}
