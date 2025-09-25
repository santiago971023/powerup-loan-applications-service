package co.com.pragma.usecase.procces_calculation;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase es el resultado que envía la lambda
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResultMessage {
    private Long applicationId;
    private ApplicationStatus newStatus;
    private String reason;
}