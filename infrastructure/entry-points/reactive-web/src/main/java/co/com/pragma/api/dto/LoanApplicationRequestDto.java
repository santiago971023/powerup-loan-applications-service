package co.com.pragma.api.dto;

import co.com.pragma.model.loanapplication.ApplicationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationRequestDto {

    @NotBlank(message = "El campo 'documento de identidad' no puede ser vacío o nulo.")
    private String idDocument;

    @NotBlank(message = "El campo 'id del tipo de préstamo' no puede ser vacío o nulo.")
    private Long loanProductId;

    @NotBlank(message = "El campo 'monto' no puede ser vacío o nulo.")
    @DecimalMin(value = "1.0", message = "El monto solicitado debe ser mayor que cero.")
    private BigDecimal loanAmount;

    @NotNull(message = "El campo 'plazo' no puede ser nulo.")
    @Min(value = 1, message = "El plazo debe ser de al menos 1 mes.")
    private Integer termInMonths;

}
