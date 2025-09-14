package co.com.pragma.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanApplicationDetailResponseDto {

    // Información de la solicitud
    private BigDecimal amount;
    private Integer termInMonths;
    private String applicationStatus;

    // Información del producto (tipo de pres´tamo)
    private String loanProductName;
    private BigDecimal interestRate;

    // Info del usuario
    private String userEmail;
    private String userName;
    private BigDecimal userSalary;

    // campo calculado
    private BigDecimal monthlyPayment;

}
