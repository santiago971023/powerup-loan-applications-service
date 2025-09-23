package co.com.pragma.usecase;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanproduct.LoanProduct;
import co.com.pragma.model.loanproduct.gateways.LoanProductRepository;
import co.com.pragma.model.pageable.DomainPageable;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListApplicationsUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private UserRepository userRepository;
    private ListApplicationsUseCase listApplicationsUseCase;
    private LoanProductRepository loanProductRepository;

    @BeforeEach
    void setup() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        userRepository = mock(UserRepository.class);
        loanProductRepository = mock(LoanProductRepository.class);
        listApplicationsUseCase = new ListApplicationsUseCase(loanApplicationRepository, userRepository, loanProductRepository);
    }

    @Test
    @DisplayName("Debería retornar una solicitud con el pago mensual calculado.")
    void shouldReturnApplicationWithCalculatedMonthlyPayment() {
        // Arrange
        LoanApplication loanApplication = LoanApplication.builder()
                .id(1L)
                .userId(10L)
                .loanProductId(20L)
                .loanAmount(BigDecimal.valueOf(10000))
                .termInMonths(10)
                .status(ApplicationStatus.APPROVED)
                .build();

        User user = User.builder()
                .id(10L)
                .name("John Doe")
                .build();
        LoanProduct loanProduct = LoanProduct.builder()
                .id(20L)
                .interestRate(BigDecimal.valueOf(0.10))
                .build();

        when(loanApplicationRepository.findByStatusIn(any(), any()))
                .thenReturn(Flux.just(loanApplication));
        when(loanProductRepository.findById(20L)).thenReturn(Mono.just(loanProduct));
        when(userRepository.findUserById(10L)).thenReturn(Mono.just(user));

        // Act and assert
        StepVerifier.create(listApplicationsUseCase.listApplicationsByStatus(
                Collections.singletonList(ApplicationStatus.APPROVED),
                DomainPageable.builder().pageNumber(0).pageSize(10).build()
        )).expectNextMatches(detail ->
                detail.getLoanApplication().getId().equals(1L)  &&
                detail.getLoanProduct().getInterestRate().equals(BigDecimal.valueOf(0.10)) &&
                detail.getUser().getName().equals("John Doe") &&
                detail.getMonthlyPayment().compareTo(BigDecimal.valueOf(1100.00)) == 0
        ).verifyComplete();
    }

    @Test
    void shouldReturnFluxOfLoanApplicationDetails(){

        // Arrange
        LoanApplication app1 = LoanApplication.builder()
                .id(1L)
                .userId(100L)
                .loanProductId(200L)
                .loanAmount(BigDecimal.valueOf(5000))
                .termInMonths(5)
                .status(ApplicationStatus.PENDING_REVIEW)
                .build();

        LoanApplication app2 = LoanApplication.builder()
                .id(2L)
                .userId(101L)
                .loanProductId(201L)
                .loanAmount(BigDecimal.valueOf(10000))
                .termInMonths(10)
                .status(ApplicationStatus.PENDING_REVIEW)
                .build();

        User user1 = User.builder().id(100L).name("Alice").build();
        User user2 = User.builder().id(101L).name("Bob").build();

        LoanProduct product1 = LoanProduct.builder()
                .id(200L)
                .interestRate(BigDecimal.valueOf(0.10))
                .build();

        LoanProduct product2 = LoanProduct.builder()
                .id(201L)
                .interestRate(BigDecimal.valueOf(0.20))
                .build();

        when(loanApplicationRepository.findByStatusIn(any(), any()))
                .thenReturn(Flux.just(app1, app2));
        when(userRepository.findUserById(100L)).thenReturn(Mono.just(user1));
        when(userRepository.findUserById(101L)).thenReturn(Mono.just(user2));
        when(loanProductRepository.findById(200L)).thenReturn(Mono.just(product1));
        when(loanProductRepository.findById(201L)).thenReturn(Mono.just(product2));

        // Act & Assert
        StepVerifier.create(
                        listApplicationsUseCase.listApplicationsByStatus(
                                List.of(ApplicationStatus.PENDING_REVIEW),
                                DomainPageable.builder().pageNumber(0).pageSize(5).build()
                        )
                )
                .expectNextMatches(detail ->
                        detail.getLoanApplication().getId().equals(1L) &&
                                detail.getUser().getName().equals("Alice") &&
                                detail.getLoanProduct().getInterestRate().equals(BigDecimal.valueOf(0.10)) &&
                                // (5000 * 1.10) / 5 = 1100.00
                                detail.getMonthlyPayment().compareTo(BigDecimal.valueOf(1100.00)) == 0
                )
                .expectNextMatches(detail ->
                        detail.getLoanApplication().getId().equals(2L) &&
                                detail.getUser().getName().equals("Bob") &&
                                detail.getLoanProduct().getInterestRate().equals(BigDecimal.valueOf(0.20)) &&
                                // (10000 * 1.20) / 10 = 1200.00
                                detail.getMonthlyPayment().compareTo(BigDecimal.valueOf(1200.00)) == 0
                )
                .verifyComplete();

        // Verificar que los mocks se llamaron
        verify(loanApplicationRepository).findByStatusIn(any(), any());
        verify(userRepository, times(2)).findUserById(any());
        verify(loanProductRepository, times(2)).findById(any());
    }

    @Test
    void shouldReturnEmptyFluxWhenNoApplicationsFound(){

        // Arrange
        when(loanApplicationRepository.findByStatusIn(any(), any()))
                .thenReturn(Flux.empty());

        // Act and assert
        StepVerifier.create(
                listApplicationsUseCase.listApplicationsByStatus(
                        List.of(ApplicationStatus.PENDING_REVIEW),
                        DomainPageable.builder().pageNumber(0).pageSize(5).build()
                )
        ).verifyComplete();

        verify(loanApplicationRepository).findByStatusIn(any(), any());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(loanProductRepository);

    }

}