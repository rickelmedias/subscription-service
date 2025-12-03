package com.example.subscription.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de conclusão de curso.
 * 
 * <h2>Clean Architecture - Application Layer:</h2>
 * <ul>
 *   <li><b>Request DTO</b>: Dados de entrada da API</li>
 *   <li><b>Validação</b>: Bean Validation com anotações</li>
 *   <li><b>Documentação</b>: OpenAPI/Swagger Schema</li>
 * </ul>
 * 
 * <h2>Validações:</h2>
 * <ul>
 *   <li>@NotNull - Campo obrigatório</li>
 *   <li>@DecimalMin(0.0) - Mínimo permitido</li>
 *   <li>@DecimalMax(10.0) - Máximo permitido</li>
 * </ul>
 * 
 * @author Guilherme
 * @see com.example.subscription.domain.valueobject.CourseAverage Validação adicional no domínio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para completar um curso")
public class CourseCompletionRequestDTO {
    
    @NotNull(message = "Average is required")
    @DecimalMin(value = "0.0", message = "Average must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Average must be at most 10.0")
    @Schema(
        description = "Média obtida no curso", 
        example = "8.5",
        minimum = "0.0",
        maximum = "10.0"
    )
    private double average;
}