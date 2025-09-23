package co.com.pragma.r2dbc.helper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Table("loan_products")
public class LoanProductEntity {

    @Id
    private Long id;
    private String name;

    @Column("min_amount")
    private BigDecimal minAmount;

    @Column("max_amount")
    private BigDecimal maxAmount;

    @Column("min_term_in_months")
    private Integer minTermInMonths;

    @Column("max_term_in_months")
    private Integer maxTermInMonths;

    @Column("interest_rate")
    private BigDecimal interestRate;

    @Column("automatic_validation")
    private boolean automaticValidation;

}
