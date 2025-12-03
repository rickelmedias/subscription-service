package com.example.subscription.application.service;

import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o CourseRecommendationService.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Rickelme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Geração de recomendações básicas para estudante</li>
 *   <li>Geração de recomendações com contexto adicional</li>
 *   <li>Tratamento de estudante não encontrado</li>
 *   <li>Construção correta de prompts</li>
 *   <li>Integração com ChatLanguageModel (mockado)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseRecommendationService Unit Tests - @Rickelme")
class CourseRecommendationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ChatLanguageModel chatLanguageModel;

    @InjectMocks
    private CourseRecommendationService recommendationService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student("João Silva");
        testStudent.setId(1L);
        // Simula que o estudante já completou alguns cursos (média 8.0 para ser aprovado)
        testStudent.completeCourse(8.0);
        testStudent.completeCourse(8.0);
        // Ajusta créditos para um valor conhecido (cada curso aprovado dá 5 créditos)
        // Após 2 cursos aprovados: 10 créditos
    }

    @Nested
    @DisplayName("recommendCoursesForStudent")
    class RecommendCoursesForStudent {

        @Test
        @DisplayName("Should return recommendations for valid student")
        void shouldReturnRecommendationsForValidStudent() {
            // Given
            String expectedRecommendation = "1. Python Avançado\n2. Machine Learning\n3. Data Science";
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn(expectedRecommendation);

            // When
            String result = recommendationService.recommendCoursesForStudent(1L);

            // Then
            assertThat(result).isEqualTo(expectedRecommendation);
            verify(studentRepository).findById(1L);
            verify(chatLanguageModel).generate(anyString());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when student not found")
        void shouldThrowExceptionWhenStudentNotFound() {
            // Given
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> recommendationService.recommendCoursesForStudent(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Student not found: 999");

            verify(studentRepository).findById(999L);
            verify(chatLanguageModel, never()).generate(anyString());
        }

        @Test
        @DisplayName("Should include student name in prompt")
        void shouldIncludeStudentNameInPrompt() {
            // Given
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesForStudent(1L);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            assertThat(promptCaptor.getValue()).contains("João Silva");
        }

        @Test
        @DisplayName("Should include completed courses count in prompt")
        void shouldIncludeCompletedCoursesInPrompt() {
            // Given
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesForStudent(1L);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            assertThat(promptCaptor.getValue()).contains("2"); // completedCourses
        }

        @Test
        @DisplayName("Should include credits in prompt")
        void shouldIncludeCreditsInPrompt() {
            // Given
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesForStudent(1L);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            // testStudent tem 2 cursos aprovados x 3 créditos = 6 créditos
            assertThat(promptCaptor.getValue()).contains("6"); // credits
        }

        @Test
        @DisplayName("Should handle student with zero completed courses")
        void shouldHandleStudentWithZeroCompletedCourses() {
            // Given
            Student newStudent = new Student("Maria");
            newStudent.setId(2L);
            
            when(studentRepository.findById(2L)).thenReturn(Optional.of(newStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Starter courses");

            // When
            String result = recommendationService.recommendCoursesForStudent(2L);

            // Then
            assertThat(result).isEqualTo("Starter courses");
        }
    }

    @Nested
    @DisplayName("recommendCoursesWithContext")
    class RecommendCoursesWithContext {

        @Test
        @DisplayName("Should return recommendations with context for valid student")
        void shouldReturnRecommendationsWithContextForValidStudent() {
            // Given
            String context = "Quero me especializar em backend";
            String expectedRecommendation = "1. Spring Boot\n2. Microservices\n3. APIs REST";
            
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn(expectedRecommendation);

            // When
            String result = recommendationService.recommendCoursesWithContext(1L, context);

            // Then
            assertThat(result).isEqualTo(expectedRecommendation);
            verify(studentRepository).findById(1L);
            verify(chatLanguageModel).generate(anyString());
        }

        @Test
        @DisplayName("Should include context in prompt")
        void shouldIncludeContextInPrompt() {
            // Given
            String context = "Interesse em Data Science";
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesWithContext(1L, context);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            assertThat(promptCaptor.getValue()).contains(context);
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when student not found with context")
        void shouldThrowExceptionWhenStudentNotFoundWithContext() {
            // Given
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> recommendationService.recommendCoursesWithContext(999L, "Any context"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Student not found: 999");

            verify(studentRepository).findById(999L);
            verify(chatLanguageModel, never()).generate(anyString());
        }

        @Test
        @DisplayName("Should include student profile in contextual prompt")
        void shouldIncludeStudentProfileInContextualPrompt() {
            // Given
            String context = "Frontend development";
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesWithContext(1L, context);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            String prompt = promptCaptor.getValue();
            
            assertThat(prompt)
                    .contains("João Silva")
                    .contains(context)
                    .contains("Créditos");
        }

        @Test
        @DisplayName("Should handle empty context")
        void shouldHandleEmptyContext() {
            // Given
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Generic recommendations");

            // When
            String result = recommendationService.recommendCoursesWithContext(1L, "");

            // Then
            assertThat(result).isEqualTo("Generic recommendations");
        }

        @Test
        @DisplayName("Should handle null context")
        void shouldHandleNullContext() {
            // Given
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            String result = recommendationService.recommendCoursesWithContext(1L, null);

            // Then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Prompt Building")
    class PromptBuilding {

        @Test
        @DisplayName("Should build recommendation prompt with correct format")
        void shouldBuildRecommendationPromptWithCorrectFormat() {
            // Given
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesForStudent(1L);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            String prompt = promptCaptor.getValue();
            
            assertThat(prompt)
                    .contains("assistente educacional")
                    .contains("Perfil do Aluno")
                    .contains("recomende 3 cursos")
                    .contains("Nome do curso")
                    .contains("Por que é relevante")
                    .contains("Dificuldade estimada");
        }

        @Test
        @DisplayName("Should build contextual prompt with correct format")
        void shouldBuildContextualPromptWithCorrectFormat() {
            // Given
            String context = "Machine Learning";
            ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesWithContext(1L, context);

            // Then
            verify(chatLanguageModel).generate(promptCaptor.capture());
            String prompt = promptCaptor.getValue();
            
            assertThat(prompt)
                    .contains("Contexto/Interesse do Aluno")
                    .contains(context)
                    .contains("considerando seu interesse");
        }
    }

    @Nested
    @DisplayName("Service Integration")
    class ServiceIntegration {

        @Test
        @DisplayName("Should call repository before LLM")
        void shouldCallRepositoryBeforeLLM() {
            // Given
            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(chatLanguageModel.generate(anyString())).thenReturn("Recommendations");

            // When
            recommendationService.recommendCoursesForStudent(1L);

            // Then
            var inOrder = inOrder(studentRepository, chatLanguageModel);
            inOrder.verify(studentRepository).findById(1L);
            inOrder.verify(chatLanguageModel).generate(anyString());
        }

        @Test
        @DisplayName("Should not call LLM if student not found")
        void shouldNotCallLLMIfStudentNotFound() {
            // Given
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> recommendationService.recommendCoursesForStudent(999L))
                    .isInstanceOf(NoSuchElementException.class);

            verify(chatLanguageModel, never()).generate(anyString());
        }
    }
}

