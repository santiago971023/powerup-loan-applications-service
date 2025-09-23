package co.com.pragma.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CalculationRequestData {

    private Long applicationId;
    private Long userId;
    private BigDecimal userSalary;
    private Long productId;
    private BigDecimal loanAmount;
    private int termInMonths;
    private BigDecimal interestRate;

}
