package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.helper.LoanApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationEntityMapper {

    LoanApplication toDomain(LoanApplicationEntity entity);

    LoanApplicationEntity toEntity(LoanApplication domain);
}
