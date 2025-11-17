package com.example.subscription.application.service;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste UNITÁRIO de Service usando Mocks
 * Rápido, sem banco de dados, isolado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Gamification Service Unit Tests")
class GamificationServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private GamificationService service;

    @Test
    @DisplayName("Should complete course and award credits when average > 7.0")
    void shouldAwardCreditsForPassingGrade() {
        // Given
        Student student = new Student("Ana", 2);
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(8.5);
        
        // When
        StudentDTO result = service.completeCourse(1L, request);
        
        // Then
        assertThat(result.getCredits()).isEqualTo(5); // 2 + 3
        assertThat(result.getCompletedCourses()).isEqualTo(1);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should NOT award credits when average = 7.0")
    void shouldNotAwardCreditsForExactThreshold() {
        // Given
        Student student = new Student("Bob", 3);
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(7.0);
        
        // When
        StudentDTO result = service.completeCourse(1L, request);
        
        // Then
        assertThat(result.getCredits()).isEqualTo(3); // Sem mudança
        assertThat(result.getCompletedCourses()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should NOT award credits when average < 7.0")
    void shouldNotAwardCreditsForFailingGrade() {
        // Given
        Student student = new Student("Carlos", 5);
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(6.9);
        
        // When
        StudentDTO result = service.completeCourse(1L, request);
        
        // Then
        assertThat(result.getCredits()).isEqualTo(5); // Sem mudança
        assertThat(result.getCompletedCourses()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception for invalid average < 0")
    void shouldThrowExceptionForNegativeAverage() {
        // Given: A validação ocorre antes da busca no repositório.
        // O mock de findById é desnecessário.
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(-0.1);
        
        // When / Then
        assertThatThrownBy(() -> service.completeCourse(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Average must be a value between 0.0 and 10.0.");
            
        // Verifica que o findById nunca foi chamado
        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception for invalid average > 10")
    void shouldThrowExceptionForAverageTooHigh() {
        // Given: A validação ocorre antes da busca no repositório.
        // O mock de findById é desnecessário.
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(10.1);
        
        // When / Then
        assertThatThrownBy(() -> service.completeCourse(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Average must be a value between 0.0 and 10.0.");
            
        // Verifica que o findById nunca foi chamado
        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(8.0);
        
        // When / Then
        assertThatThrownBy(() -> service.completeCourse(999L, request))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Student not found: 999");
            
        // Verifica que o findById foi chamado
        verify(repository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should increment completed courses counter")
    void shouldIncrementCompletedCoursesCounter() {
        // Given
        Student student = new Student("Diana", 0);
        when(repository.findById(1L))
            .thenReturn(Optional.of(student)) // Simula 3 chamadas, sempre retornando o mesmo objeto
            .thenReturn(Optional.of(student))
            .thenReturn(Optional.of(student));
        
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(9.0);
        
        // When
        service.completeCourse(1L, request);
        service.completeCourse(1L, request);
        service.completeCourse(1L, request);
        
        // Then
        assertThat(student.getCompletedCourses()).isEqualTo(3);
        assertThat(student.getCredits()).isEqualTo(9); // 3 cursos × 3 créditos
        verify(repository, times(3)).findById(1L);
    }
}