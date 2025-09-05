package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationResponseDto {

    private Long id;

    private Long userId;

    private Long loanProductId;

    private BigDecimal amount;

    private Integer termInMonths;

    private String status;

    private LocalDateTime creationDate;
}
