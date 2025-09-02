package co.com.pragma.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Long id;
    private Long userId;
    private Long loanProductId;
    private BigDecimal loanAmount;
    private Integer termInMonths;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
}
