package co.com.pragma.model;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanproduct.LoanProduct;
import co.com.pragma.model.user.User;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationDetail {

    private LoanApplication loanApplication;
    private LoanProduct loanProduct;
    private User user;
    private BigDecimal monthlyPayment;


}
