package com.example.subscription.presentation.controller;

import com.example.subscription.application.service.CourseRecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para o RecommendationController.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Guilherme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>GET /recommendations/students/{id} - Recomendações básicas</li>
 *   <li>POST /recommendations/students/{id} - Recomendações com contexto</li>
 *   <li>GET /recommendations/health - Health check</li>
 *   <li>Tratamento de erros (404, etc.)</li>
 * </ul>
 */
@WebMvcTest(RecommendationController.class)
@DisplayName("RecommendationController Unit Tests - @Guilherme")
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseRecommendationService recommendationService;

    @Nested
    @DisplayName("GET /recommendations/students/{id}")
    class GetRecommendations {

        @Test
        @DisplayName("Should return recommendations for valid student")
        void shouldReturnRecommendationsForValidStudent() throws Exception {
            // Given
            Long studentId = 1L;
            String expectedRecommendations = "1. Python Avançado - Relevante para sua trilha\n" +
                    "2. Machine Learning Básico - Próximo passo natural\n" +
                    "3. Data Science com Python - Complementa seu aprendizado";
            
            when(recommendationService.recommendCoursesForStudent(studentId))
                    .thenReturn(expectedRecommendations);

            // When / Then
            mockMvc.perform(get("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId", is(1)))
                    .andExpect(jsonPath("$.recommendations", is(expectedRecommendations)));
        }

        @Test
        @DisplayName("Should return 404 when student not found")
        void shouldReturn404WhenStudentNotFound() throws Exception {
            // Given
            Long studentId = 999L;
            when(recommendationService.recommendCoursesForStudent(studentId))
                    .thenThrow(new NoSuchElementException("Student not found: " + studentId));

            // When / Then
            mockMvc.perform(get("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle empty recommendations")
        void shouldHandleEmptyRecommendations() throws Exception {
            // Given
            Long studentId = 1L;
            when(recommendationService.recommendCoursesForStudent(studentId))
                    .thenReturn("");

            // When / Then
            mockMvc.perform(get("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId", is(1)))
                    .andExpect(jsonPath("$.recommendations", is("")));
        }
    }

    @Nested
    @DisplayName("POST /recommendations/students/{id}")
    class GetRecommendationsWithContext {

        @Test
        @DisplayName("Should return recommendations with context for valid student")
        void shouldReturnRecommendationsWithContextForValidStudent() throws Exception {
            // Given
            Long studentId = 1L;
            String context = "Quero me especializar em backend com Java";
            String expectedRecommendations = "1. Spring Boot Avançado - Ideal para backend Java\n" +
                    "2. Microservices com Spring Cloud\n" +
                    "3. JPA e Hibernate Avançado";
            
            when(recommendationService.recommendCoursesWithContext(eq(studentId), eq(context)))
                    .thenReturn(expectedRecommendations);

            // When / Then
            mockMvc.perform(post("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"context\": \"" + context + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId", is(1)))
                    .andExpect(jsonPath("$.recommendations", is(expectedRecommendations)));
        }

        @Test
        @DisplayName("Should return 404 when student not found with context")
        void shouldReturn404WhenStudentNotFoundWithContext() throws Exception {
            // Given
            Long studentId = 999L;
            when(recommendationService.recommendCoursesWithContext(eq(studentId), anyString()))
                    .thenThrow(new NoSuchElementException("Student not found: " + studentId));

            // When / Then
            mockMvc.perform(post("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"context\": \"Any context\"}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle null context")
        void shouldHandleNullContext() throws Exception {
            // Given
            Long studentId = 1L;
            String expectedRecommendations = "Generic recommendations";
            
            when(recommendationService.recommendCoursesWithContext(eq(studentId), eq(null)))
                    .thenReturn(expectedRecommendations);

            // When / Then
            mockMvc.perform(post("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"context\": null}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recommendations", is(expectedRecommendations)));
        }

        @Test
        @DisplayName("Should handle empty context")
        void shouldHandleEmptyContext() throws Exception {
            // Given
            Long studentId = 1L;
            String expectedRecommendations = "Recommendations without specific context";
            
            when(recommendationService.recommendCoursesWithContext(eq(studentId), eq("")))
                    .thenReturn(expectedRecommendations);

            // When / Then
            mockMvc.perform(post("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"context\": \"\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recommendations", is(expectedRecommendations)));
        }
    }

    @Nested
    @DisplayName("GET /recommendations/health")
    class HealthCheck {

        @Test
        @DisplayName("Should return health status when Ollama is available")
        void shouldReturnHealthStatusWhenOllamaIsAvailable() throws Exception {
            // When / Then
            mockMvc.perform(get("/recommendations/health")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Ollama service is available"));
        }
    }

    @Nested
    @DisplayName("Response DTO Tests")
    class ResponseDtoTests {

        @Test
        @DisplayName("Should serialize RecommendationResponse correctly")
        void shouldSerializeRecommendationResponseCorrectly() throws Exception {
            // Given
            Long studentId = 42L;
            String recommendations = "Test recommendations";
            
            when(recommendationService.recommendCoursesForStudent(studentId))
                    .thenReturn(recommendations);

            // When / Then
            mockMvc.perform(get("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId", is(42)))
                    .andExpect(jsonPath("$.recommendations", is(recommendations)));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle service exception gracefully")
        void shouldHandleServiceExceptionGracefully() throws Exception {
            // Given
            Long studentId = 1L;
            when(recommendationService.recommendCoursesForStudent(anyLong()))
                    .thenThrow(new RuntimeException("Ollama service unavailable"));

            // When / Then
            mockMvc.perform(get("/recommendations/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }
}

