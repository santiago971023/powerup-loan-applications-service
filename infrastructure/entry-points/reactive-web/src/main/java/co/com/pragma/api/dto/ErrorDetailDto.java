package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO de respuesta, detalla un error.")
public class ErrorDetailDto {

    @Schema(description = "Nombre del campo que falla la validación", example = "'name'")
    private String field;

    @Schema(description = "Explicación de error.", example = "El campo 'name' no puede ser nulo.")
    private String description;

}
