package co.com.pragma.r2dbc;

import co.com.pragma.model.loanproduct.LoanProduct;
import co.com.pragma.model.loanproduct.gateways.LoanProductRepository;
import co.com.pragma.r2dbc.helper.LoanProductEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.function.Function;

@Repository
public class LoanProductR2dbcRepositoryAdapter extends ReactiveAdapterOperations<
        LoanProduct, LoanProductEntity, Long, LoanProductR2dbcRepository>
        implements LoanProductRepository {

    protected LoanProductR2dbcRepositoryAdapter(LoanProductR2dbcRepository repository, ObjectMapper mapper) {
        super(repository, mapper, LoanProductEntity -> mapper.map(LoanProductEntity, LoanProduct.class));
    }
}
