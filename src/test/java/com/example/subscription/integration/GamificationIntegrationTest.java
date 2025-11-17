package com.example.subscription.integration;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração End-to-End.
 * Testa toda a stack: Controller -> Service -> Repository -> Database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - Complete Flow")
class GamificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should complete course and update credits - Full Integration")
    void shouldCompleteCourseAndUpdateCreditsFullIntegration() throws Exception {
        // Given - Cria estudante no banco
        Student student = new Student("Ana Silva", 2);
        student = studentRepository.save(student);
        Long studentId = student.getId();

        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO(8.5);

        // When - Faz requisição HTTP
        mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.completedCourses").value(1))
                .andExpect(jsonPath("$.credits").value(5)); // 2 + 3

        // Then - Verifica no banco de dados
        Student updated = studentRepository.findById(studentId).orElseThrow();
        assertThat(updated.getCompletedCourses()).isEqualTo(1);
        assertThat(updated.getCredits()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should list all students - Full Integration")
    void shouldListAllStudentsFullIntegration() throws Exception {
        // Given
        studentRepository.save(new Student("Alice", 5));
        studentRepository.save(new Student("Bob", 10));
        studentRepository.save(new Student("Charlie", 15));

        // When/Then
        mockMvc.perform(get("/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice", "Bob", "Charlie")))
                .andExpect(jsonPath("$[*].credits", containsInAnyOrder(5, 10, 15)));
    }

    @Test
    @DisplayName("Should get student by ID - Full Integration")
    void shouldGetStudentByIdFullIntegration() throws Exception {
        // Given
        Student student = studentRepository.save(new Student("Diana", 12));

        // When/Then
        mockMvc.perform(get("/students/{id}", student.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value("Diana"))
                .andExpect(jsonPath("$.credits").value(12));
    }

    @Test
    @DisplayName("Should return 404 when student not found")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        mockMvc.perform(get("/students/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Should return 400 for invalid average")
    void shouldReturn400ForInvalidAverage() throws Exception {
        // Given
        Student student = studentRepository.save(new Student("Test", 0));
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO(10.5);

        // When/Then
        mockMvc.perform(post("/gamification/students/{id}/complete-course", student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("Should handle multiple course completions sequentially")
    void shouldHandleMultipleCourseCompletionsSequentially() throws Exception {
        // Given
        Student student = studentRepository.save(new Student("Eduardo", 0));
        Long studentId = student.getId();

        // When - Completa 3 cursos
        CourseCompletionRequestDTO request1 = new CourseCompletionRequestDTO(8.0);
        CourseCompletionRequestDTO request2 = new CourseCompletionRequestDTO(9.0);
        CourseCompletionRequestDTO request3 = new CourseCompletionRequestDTO(6.5);

        mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedCourses").value(3))
                .andExpect(jsonPath("$.credits").value(6)); // 0 + 3 + 3 + 0

        // Then - Verifica no banco
        Student updated = studentRepository.findById(studentId).orElseThrow();
        assertThat(updated.getCompletedCourses()).isEqualTo(3);
        assertThat(updated.getCredits()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should persist changes across transactions")
    void shouldPersistChangesAcrossTransactions() throws Exception {
        // Given
        Student student = studentRepository.save(new Student("Fernanda", 5));
        Long studentId = student.getId();

        // When - Primeira transação
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO(8.5);
        mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then - Segunda transação (nova busca)
        mockMvc.perform(get("/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credits").value(8)) // 5 + 3
                .andExpect(jsonPath("$.completedCourses").value(1));
    }

    @Test
    @DisplayName("Should return empty list when no students exist")
    void shouldReturnEmptyListWhenNoStudentsExist() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle concurrent requests safely")
    void shouldHandleConcurrentRequestsSafely() throws Exception {
        // Given
        Student student = studentRepository.save(new Student("Gabriel", 0));
        Long studentId = student.getId();
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO(8.5);

        // When - Simula requisições "concorrentes" (sequenciais mas rápidas)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/gamification/students/{id}/complete-course", studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // Then
        Student updated = studentRepository.findById(studentId).orElseThrow();
        assertThat(updated.getCompletedCourses()).isEqualTo(5);
        assertThat(updated.getCredits()).isEqualTo(15); // 5 × 3
    }
}