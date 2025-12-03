package com.example.subscription.application.service;

import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Application Service para gerenciamento de Estudantes.
 * 
 * <h2>Clean Architecture - Application Layer:</h2>
 * <ul>
 *   <li><b>Orquestração</b>: Coordena operações entre Repository e Domain</li>
 *   <li><b>DTO Mapping</b>: Converte Entity ↔ DTO</li>
 *   <li><b>Transaction Management</b>: Gerencia transações com @Transactional</li>
 *   <li><b>Dependency Inversion</b>: Depende de abstrações (Repository interface)</li>
 * </ul>
 * 
 * <h2>Responsabilidades:</h2>
 * <ul>
 *   <li>Listar todos os estudantes</li>
 *   <li>Buscar estudante por ID</li>
 * </ul>
 * 
 * @author Rickelme
 * @see StudentDTO DTO de transferência de dados
 * @see StudentRepository Repositório de acesso a dados
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

    /**
     * Cria um novo estudante
     * 
     * @param dto dados do estudante
     * @return DTO do estudante criado
     */
    @Transactional
    public StudentDTO createStudent(StudentDTO dto) {
        Student student = new Student(dto.getName());
        Student saved = studentRepository.save(student);
        return StudentDTO.fromEntity(saved);
    }
}