package co.com.pragma.usecase.saveloanapplication;

import co.com.pragma.model.LoanApplicationDetail;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanproduct.LoanProduct;
import co.com.pragma.model.loanproduct.gateways.LoanProductRepository;
import co.com.pragma.model.pageable.DomainPageable;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class ListApplicationsUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final LoanProductRepository loanProductRepository;

    public Flux<LoanApplicationDetail> listApplicationsByStatus(List<ApplicationStatus> statuses, DomainPageable pageable) {

        return loanApplicationRepository.findByStatusIn(statuses, pageable)
                .flatMap(this::enrichApplicationWithDetails);
    }




    private Mono<LoanApplicationDetail> enrichApplicationWithDetails(LoanApplication loanApplication) {

        Mono<User> userMono = userRepository.findUserById(loanApplication.getUserId())
                .defaultIfEmpty(new User());

        Mono<LoanProduct> productMono = loanProductRepository.findById(loanApplication.getLoanProductId())
                .defaultIfEmpty(new LoanProduct());

        return Mono.zip(userMono, productMono)
                .map(tuple -> {
                    User user = tuple.getT1();
                    LoanProduct loanProduct = tuple.getT2();

                    BigDecimal monthlyPayment = calculateMonthlyPayment(loanApplication.getLoanAmount(),
                            loanProduct.getInterestRate(),
                            loanApplication.getTermInMonths());

                    return LoanApplicationDetail.builder()
                            .loanApplication(loanApplication)
                            .user(user)
                            .loanProduct(loanProduct)
                            .monthlyPayment(monthlyPayment)
                            .build();
                });
    }


    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal interestRate, Integer term) {
        if (amount == null || interestRate == null || term == null || term == 0) {
            return BigDecimal.ZERO;
        }
        // Fórmula de interés simple para el ejemplo: (Capital * (1 + Tasa)) / Plazo
        BigDecimal totalAmount = amount.multiply(BigDecimal.ONE.add(interestRate));
        return totalAmount.divide(BigDecimal.valueOf(term), 2, RoundingMode.HALF_UP);
    }


}
