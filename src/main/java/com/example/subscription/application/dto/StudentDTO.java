package com.example.subscription.application.dto;

import com.example.subscription.domain.entity.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados do Student.
 * Isola a camada de apresentação do domínio (SOLID - Dependency Inversion)
 * 
 * Rickelme
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