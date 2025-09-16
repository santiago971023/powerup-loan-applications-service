package co.com.pragma.r2dbc;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.pageable.DomainPageable;
import co.com.pragma.r2dbc.helper.LoanApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LoanApplicationR2dbcRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication, LoanApplicationEntity, Long, LoanApplicationR2dbcRepository>
        implements LoanApplicationRepository {

    protected LoanApplicationR2dbcRepositoryAdapter(
            LoanApplicationR2dbcRepository repository,
            ObjectMapper mapper
    ) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }

    @Override
    public Flux<LoanApplication> findByStatusIn(List<ApplicationStatus> statuses, DomainPageable domainPageable) {

        // 1. TRADUCCIÓN: Convertimos nuestro DomainPageable al Pageable de Spring
        Pageable springPageable = PageRequest.of(
                domainPageable.getPageNumber(),
                domainPageable.getPageSize()
        );

        List<String> statusNames = statuses.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return repository.findByStatusIn(statusNames, springPageable)
                .map(this::toEntity);
    }
}
