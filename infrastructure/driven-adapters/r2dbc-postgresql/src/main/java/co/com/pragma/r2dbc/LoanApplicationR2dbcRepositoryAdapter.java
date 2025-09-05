package co.com.pragma.r2dbc;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.r2dbc.helper.LoanApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanApplicationEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

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
}
