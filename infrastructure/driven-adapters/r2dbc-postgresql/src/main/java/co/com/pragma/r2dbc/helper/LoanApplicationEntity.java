package co.com.pragma.r2dbc.helper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("loan_applications")
public class LoanApplicationEntity {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("loan_product_id")
    private Long loanProductId;

    @Column("loan_amount")
    private BigDecimal loanAmount;

    @Column("term_in_months")
    private Integer termInMonths;

    private String status;

    @Column("creation_date")
    private LocalDateTime creationDate;
}
