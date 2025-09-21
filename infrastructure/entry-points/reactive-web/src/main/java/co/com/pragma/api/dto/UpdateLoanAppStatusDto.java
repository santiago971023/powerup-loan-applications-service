package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLoanAppStatusDto {

    @NotBlank(message = "El campo 'status' no puede ser vacío o nulo.")
    private String status;

}
