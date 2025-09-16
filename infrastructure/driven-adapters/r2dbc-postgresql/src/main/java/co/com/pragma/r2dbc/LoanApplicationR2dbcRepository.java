package co.com.pragma.r2dbc;

import co.com.pragma.model.pageable.DomainPageable;
import co.com.pragma.r2dbc.helper.LoanApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
public interface LoanApplicationR2dbcRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    Flux<LoanApplicationEntity> findByStatusIn(List<String> statuses, Pageable pageable);

}
