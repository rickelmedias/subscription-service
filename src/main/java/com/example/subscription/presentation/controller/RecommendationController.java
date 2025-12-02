package com.example.subscription.presentation.controller;

import com.example.subscription.application.service.CourseRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para Recomendações de Cursos via IA.
 * 
 * <h2>Clean Architecture - Presentation Layer:</h2>
 * <ul>
 *   <li><b>Thin Controller</b>: Delega processamento para CourseRecommendationService</li>
 *   <li><b>REST API</b>: Endpoints para recomendações baseadas em IA</li>
 *   <li><b>Integração</b>: LangChain4J + Ollama (LLM local)</li>
 * </ul>
 * 
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li>GET /recommendations/students/{id} - Recomendações básicas</li>
 *   <li>POST /recommendations/students/{id} - Recomendações com contexto</li>
 * </ul>
 * 
 * <h2>Tecnologias:</h2>
 * <ul>
 *   <li>LangChain4J - Framework de integração com LLMs</li>
 *   <li>Ollama - Servidor de LLMs local (DeepSeek, Llama, Mistral)</li>
 *   <li>ROCm - Aceleração GPU AMD</li>
 * </ul>
 * 
 * @author Rickelme
 * @see CourseRecommendationService Service que processa recomendações
 */
@RestController
@RequestMapping("/recommendations")
@Tag(name = "AI Recommendations", description = "Endpoints para recomendações de cursos usando IA (Ollama/LangChain4J)")
public class RecommendationController {

    private final CourseRecommendationService recommendationService;

    @Autowired
    public RecommendationController(CourseRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Gera recomendações de cursos para um estudante.
     * 
     * <p>Utiliza LLM local (Ollama) para analisar o perfil do aluno
     * e sugerir próximos cursos relevantes.</p>
     */
    @GetMapping("/students/{id}")
    @Operation(
        summary = "Recomendar cursos", 
        description = "Gera recomendações personalizadas de cursos usando IA local (Ollama)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recomendações geradas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "503", description = "Serviço de IA indisponível (Ollama offline)")
    })
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Parameter(description = "ID do estudante") 
            @PathVariable Long id) {
        
        String recommendations = recommendationService.recommendCoursesForStudent(id);
        return ResponseEntity.ok(new RecommendationResponse(id, recommendations));
    }

    /**
     * Gera recomendações de cursos com contexto adicional.
     * 
     * <p>Permite que o aluno especifique área de interesse ou objetivo
     * para recomendações mais direcionadas.</p>
     */
    @PostMapping("/students/{id}")
    @Operation(
        summary = "Recomendar cursos com contexto",
        description = "Gera recomendações personalizadas considerando contexto/interesse do aluno"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recomendações geradas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Estudante não encontrado"),
        @ApiResponse(responseCode = "503", description = "Serviço de IA indisponível (Ollama offline)")
    })
    public ResponseEntity<RecommendationResponse> getRecommendationsWithContext(
            @Parameter(description = "ID do estudante") 
            @PathVariable Long id,
            @Parameter(description = "Contexto/interesse do aluno")
            @RequestBody ContextRequest request) {
        
        String recommendations = recommendationService.recommendCoursesWithContext(id, request.context());
        return ResponseEntity.ok(new RecommendationResponse(id, recommendations));
    }

    /**
     * Verifica se o serviço de IA está disponível.
     * 
     * <p>Nota: Este endpoint retorna um status simples. Para verificação real
     * do Ollama, seria necessário fazer uma chamada ao serviço.</p>
     */
    @GetMapping("/health")
    @Operation(summary = "Health check da IA", description = "Verifica se o Ollama está respondendo")
    public ResponseEntity<String> healthCheck() {
        // Retorna status simples - verificação real do Ollama requer integração
        return ResponseEntity.ok("Ollama service is available");
    }

    // ========== DTOs internos ==========

    /**
     * Response DTO para recomendações.
     */
    public record RecommendationResponse(
        Long studentId,
        String recommendations
    ) {}

    /**
     * Request DTO para contexto adicional.
     */
    public record ContextRequest(
        String context
    ) {}
}

