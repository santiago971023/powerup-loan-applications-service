package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.pageable.DomainPageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(Long id);
    Flux<LoanApplication> findByStatusIn(List<ApplicationStatus> statuses, DomainPageable pageable);
}
