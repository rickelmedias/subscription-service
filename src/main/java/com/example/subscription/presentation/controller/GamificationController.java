package com.example.subscription.presentation.controller;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.application.service.GamificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para funcionalidades de Gamificação.
 * 
 * <h2>Clean Architecture - Presentation Layer:</h2>
 * <ul>
 *   <li><b>Thin Controller</b>: Delega processamento para GamificationService</li>
 *   <li><b>Validação</b>: Usa @Valid para validar DTOs automaticamente</li>
 *   <li><b>REST API</b>: Endpoint POST para ação de completar curso</li>
 * </ul>
 * 
 * <h2>Endpoint:</h2>
 * <ul>
 *   <li>POST /gamification/students/{id}/complete-course - Completa curso e aplica gamificação</li>
 * </ul>
 * 
 * <h2>Fluxo de Dados:</h2>
 * <pre>
 * HTTP Request → Controller → Service → Domain → Repository → Database
 *                    ↓
 *              Validation (@Valid)
 * </pre>
 * 
 * @author Guilherme
 * @see GamificationService Service que processa a gamificação
 * @see CourseCompletionRequestDTO DTO de entrada validado
 */
@RestController
@RequestMapping("/gamification")
@Tag(name = "Gamification", description = "Endpoints para gamificação de cursos")
public class GamificationController {

    private final GamificationService gamificationService;

    @Autowired
    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @PostMapping("/students/{id}/complete-course")
    @Operation(summary = "Completar curso", description = "Registra conclusão de curso e aplica gamificação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso completado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Média inválida"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado")
    })
    public ResponseEntity<StudentDTO> completeCourse(
            @PathVariable Long id, 
            @Valid @RequestBody CourseCompletionRequestDTO request) {
        
        StudentDTO updatedStudent = gamificationService.completeCourse(id, request);
        return ResponseEntity.ok(updatedStudent);
    }
}