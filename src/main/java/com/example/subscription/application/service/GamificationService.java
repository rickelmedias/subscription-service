package com.example.subscription.application.service;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.domain.valueobject.CourseAverage;
import com.example.subscription.infrastructure.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Serviço de Gamificação.
 * 
 * Responsabilidades:
 * - Aplicar regras de gamificação
 * - Coordenar entre Repository e Domain
 * - Gerenciar transações
 * 
 * Guilherme
 */
@Service
@Slf4j
public class GamificationService {

    private final StudentRepository studentRepository;

    @Autowired
    public GamificationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Completa um curso e aplica gamificação.
     * 
     * @param studentId ID do estudante
     * @param request dados da conclusão (média)
     * @return DTO com dados atualizados
     * @throws IllegalArgumentException se média inválida
     * @throws NoSuchElementException se estudante não encontrado
     */
    @Transactional
    public StudentDTO completeCourse(Long studentId, CourseCompletionRequestDTO request) {
        if (log.isInfoEnabled()) {
            log.info("Processing course completion for student ID: {}, average: {}", 
                     studentId, request.getAverage());
        }
        
        // 1. Validar entrada (cria Value Object que auto-valida)
        CourseAverage average = CourseAverage.of(request.getAverage());
        
        // 2. Buscar estudante
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Student not found with ID: {}", studentId);
                    }
                    return new NoSuchElementException("Student not found: " + studentId);
                });
        
        // 3. Aplicar lógica de negócio (no domínio)
        student.completeCourse(average);
        
        // 4. O @Transactional salva automaticamente (dirty checking do JPA)
        if (log.isInfoEnabled()) {
            log.info("Course completed successfully. Student: {}, Credits: {}", 
                     student.getName(), student.getCredits());
        }
        
        // 5. Retornar DTO
        return StudentDTO.fromEntity(student);
    }
}