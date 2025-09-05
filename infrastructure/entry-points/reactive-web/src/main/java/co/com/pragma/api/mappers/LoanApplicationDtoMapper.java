package co.com.pragma.api.mappers;

import co.com.pragma.api.dto.LoanApplicationRequestDto;
import co.com.pragma.api.dto.LoanApplicationResponseDto;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.saveloanapplication.SaveLoanApplicationUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationDtoMapper {

    SaveLoanApplicationUseCase.Input toUseCaseInput (LoanApplicationRequestDto loanApplicationDto);
    LoanApplicationResponseDto toResponse(LoanApplication loanApplication);

}
