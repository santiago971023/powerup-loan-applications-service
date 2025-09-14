package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.helper.LoanProductEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

// TODO: This file is just an example, you should delete or modify it

public interface LoanProductR2dbcRepository extends ReactiveCrudRepository<LoanProductEntity, Long>, ReactiveQueryByExampleExecutor<LoanProductEntity> {

}
