package com.example.subscription.application.service;

import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.infrastructure.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Serviço de Estudantes.
 * 
 * Responsabilidades:
 * - CRUD de estudantes
 * - Consultas e listagens
 * 
 * Rickelme
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Lista todos os estudantes
     * 
     * @return lista de DTOs
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca estudante por ID
     * 
     * @param id ID do estudante
     * @return DTO do estudante
     * @throws NoSuchElementException se não encontrado
     */
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(StudentDTO::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + id));
    }
}