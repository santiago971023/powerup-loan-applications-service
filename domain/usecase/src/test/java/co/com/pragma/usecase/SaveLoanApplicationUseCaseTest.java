package co.com.pragma.usecase;

import co.com.pragma.model.exception.LoanProductNotFoundException;
import co.com.pragma.model.exception.UserNotFoundException;
import co.com.pragma.model.loanapplication.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanproduct.LoanProduct;
import co.com.pragma.model.loanproduct.gateways.LoanProductRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.SaveLoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class SaveLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanProductRepository loanProductRepository;

    @InjectMocks
    private SaveLoanApplicationUseCase saveLoanApplicationUseCase;

    private SaveLoanApplicationUseCase.Input input;
    private User mockUser;
    private LoanApplication mockSavedLoanApp;
    private LoanProduct mockLoanProduct;

    @BeforeEach
    void setUp() {
        input = new SaveLoanApplicationUseCase.Input(
                "123456",
                1L,
                new BigDecimal("5000"),
                12
        );

        mockUser = new User();
        mockUser.setId(10L);

        mockLoanProduct = new LoanProduct();
        mockLoanProduct.setId(1L);
        mockLoanProduct.setName("Personal Loan");

        mockSavedLoanApp = new LoanApplication();
        mockSavedLoanApp.setId(100L);
        mockSavedLoanApp.setUserId(mockUser.getId());
        mockSavedLoanApp.setLoanProductId(mockLoanProduct.getId());
        mockSavedLoanApp.setLoanAmount(input.getLoanAmount());
        mockSavedLoanApp.setTermInMonths(input.getTermInMonths());
        mockSavedLoanApp.setStatus(ApplicationStatus.PENDING_REVIEW);
        mockSavedLoanApp.setCreationDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería guardar una solicitud exitosamente.")
    void saveLoanApplicationShouldBeSuccessful() {
        // Configurando mocks
        when(userRepository.findUserByIdDocument(input.getIdDocument()))
                .thenReturn(Mono.just(mockUser));

        when(loanProductRepository.findById(input.getLoanProductId()))
                .thenReturn(Mono.just(mockLoanProduct));

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(mockSavedLoanApp));

        // Ejecutando caso de uso
        Mono<LoanApplication> result = saveLoanApplicationUseCase.saveLoanApplication(input);

        // Verificando resultadoos
        StepVerifier.create(result)
                .expectNextMatches(app ->
                        app.getUserId().equals(mockUser.getId()) &&
                        app.getLoanProductId().equals(mockLoanProduct.getId()) &&
                        app.getLoanAmount().compareTo(input.getLoanAmount()) == 0 &&
                        app.getTermInMonths().equals(input.getTermInMonths()) &&
                        app.getStatus() == ApplicationStatus.PENDING_REVIEW
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando no encuentre el usuario.")
    void saveLoanApplicationShouldThrowAnExeptionUserNotFound() {

        // Configuramos el mock
        when(userRepository.findUserByIdDocument(input.getIdDocument()))
                .thenReturn(Mono.empty());

        // Ejecutar caso de uso
        Mono<LoanApplication> result = saveLoanApplicationUseCase.saveLoanApplication(input);

        // Verificar que se llama a UserNotFoundException
        StepVerifier.create(result)
                .expectErrorMatches( throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("No se encontró un usuario con este documento.")
                )
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando no encuentre el tipo de préstamo.")
    void saveLoanApplicationShouldThrowAnExeptionLoanProductNotFound() {

        // Configuramos el mock
        when(userRepository.findUserByIdDocument(input.getIdDocument()))
                .thenReturn(Mono.just(mockUser));

        when(loanProductRepository.findById(input.getLoanProductId()))
                .thenReturn(Mono.empty());

        // Ejecutar caso de uso
        Mono<LoanApplication> result = saveLoanApplicationUseCase.saveLoanApplication(input);

        // Verificar que se llama a UserNotFoundException
        StepVerifier.create(result)
                .expectErrorMatches( throwable ->
                        throwable instanceof LoanProductNotFoundException &&
                                throwable.getMessage().equals("No se encontró un producto con ese id")
                )
                .verify();
    }


}