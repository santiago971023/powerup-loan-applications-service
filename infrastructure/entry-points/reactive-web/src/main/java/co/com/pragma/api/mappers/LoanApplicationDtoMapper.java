package co.com.pragma.api.mappers;

import co.com.pragma.api.dto.LoanApplicationDetailResponseDto;
import co.com.pragma.api.dto.LoanApplicationRequestDto;
import co.com.pragma.api.dto.LoanApplicationResponseDto;
import co.com.pragma.model.LoanApplicationDetail;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.saveloanapplication.SaveLoanApplicationUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationDtoMapper {

    SaveLoanApplicationUseCase.Input toUseCaseInput (LoanApplicationRequestDto loanApplicationDto);
    LoanApplicationResponseDto toResponse(LoanApplication loanApplication);

    @Mapping(source = "loanApplication.loanAmount", target = "amount")
    @Mapping(source = "loanApplication.termInMonths", target = "termInMonths")
    @Mapping(source = "loanApplication.status", target = "applicationStatus")
    @Mapping(source = "loanProduct.name", target = "loanProductName")
    @Mapping(source = "loanProduct.interestRate", target = "interestRate")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.salary", target = "userSalary")
    // Mapeo más complejo para combinar nombre y apellido
    @Mapping(target = "userName", expression = "java(detail.getUser().getName() + \" \" + detail.getUser().getLastname())")
    LoanApplicationDetailResponseDto toDetailResponseDto(LoanApplicationDetail detail);

}
