package co.com.pragma.model.loanproduct;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanProduct {

    private Long id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTermInMonths;
    private Integer maxTermInMonths;
    private BigDecimal interestRate;
    private boolean automaticValidation;
}
