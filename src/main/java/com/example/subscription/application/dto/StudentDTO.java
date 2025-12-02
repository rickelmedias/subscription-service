package com.example.subscription.application.dto;

import com.example.subscription.domain.entity.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object para transferência de dados do Student.
 * 
 * <h2>Clean Architecture - Application Layer:</h2>
 * <ul>
 *   <li><b>Isolamento</b>: Separa camada de apresentação do domínio</li>
 *   <li><b>Dependency Inversion</b>: Controllers dependem de DTOs, não de Entities</li>
 *   <li><b>Mapper Pattern</b>: Métodos fromEntity() e toEntity()</li>
 * </ul>
 * 
 * <h2>Anotações:</h2>
 * <ul>
 *   <li>@Schema - Documentação OpenAPI/Swagger</li>
 *   <li>@Data - Lombok: getters, setters, equals, hashCode, toString</li>
 * </ul>
 * 
 * @author Rickelme
 * @see Student Entidade de domínio correspondente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do estudante")
public class StudentDTO {
    
    @Schema(description = "ID único do estudante", example = "1")
    private Long id;
    
    @Schema(description = "Nome do estudante", example = "Ana Silva")
    private String name;
    
    @Schema(description = "Quantidade de cursos completados", example = "5")
    private int completedCourses;
    
    @Schema(description = "Saldo de créditos", example = "15")
    private int credits;

    /**
     * Converte Entity para DTO (Mapper Pattern)
     */
    public static StudentDTO fromEntity(Student student) {
        if (student == null) {
            return null;
        }
        
        return new StudentDTO(
            student.getId(),
            student.getName(),
            student.getCompletedCourses(),
            student.getCredits()
        );
    }
    
    /**
     * Converte DTO para Entity (usado raramente)
     */
    public Student toEntity() {
        Student student = new Student(this.name);
        student.setId(this.id);
        student.setCredits(this.credits);
        return student;
    }
}