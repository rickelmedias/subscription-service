package com.example.subscription.presentation.controller;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.application.service.GamificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste UNITÁRIO de Controller usando MockMvc
 * Testa apenas a camada web, sem subir servidor completo
 */
@WebMvcTest(GamificationController.class)
@DisplayName("Gamification Controller Unit Tests")
class GamificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GamificationService service;

    @Test
    @DisplayName("POST /gamification/students/{id}/complete-course - Success")
    void shouldCompleteCourseSucessfully() throws Exception {
        // Given
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(9.5);
        
        StudentDTO expectedResponse = new StudentDTO(1L, "Ana", 1, 5);
        when(service.completeCourse(eq(1L), any(CourseCompletionRequestDTO.class)))
            .thenReturn(expectedResponse);
        
        // When / Then
        mockMvc.perform(post("/gamification/students/1/complete-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Ana"))
            .andExpect(jsonPath("$.completedCourses").value(1))
            .andExpect(jsonPath("$.credits").value(5));
    }

    @Test
    @DisplayName("POST /gamification/students/{id}/complete-course - Invalid Average")
    void shouldReturnBadRequestForInvalidAverage() throws Exception {
        // Given
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(-1.0);
        
        when(service.completeCourse(eq(1L), any(CourseCompletionRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Average must be a value between 0.0 and 10.0."));
        
        // When / Then
        mockMvc.perform(post("/gamification/students/1/complete-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /gamification/students/{id}/complete-course - Student Not Found")
    void shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {
        // Given
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(8.0);
        
        when(service.completeCourse(eq(999L), any(CourseCompletionRequestDTO.class)))
            .thenThrow(new NoSuchElementException("Student not found: 999"));
        
        // When / Then
        mockMvc.perform(post("/gamification/students/999/complete-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /gamification/students/{id}/complete-course - Passing Grade")
    void shouldAwardCreditsForPassingGrade() throws Exception {
        // Given
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(7.1);
        
        StudentDTO expectedResponse = new StudentDTO(2L, "Bob", 1, 3);
        when(service.completeCourse(eq(2L), any(CourseCompletionRequestDTO.class)))
            .thenReturn(expectedResponse);
        
        // When / Then
        mockMvc.perform(post("/gamification/students/2/complete-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.credits").value(3));
    }

    @Test
    @DisplayName("POST /gamification/students/{id}/complete-course - Failing Grade")
    void shouldNotAwardCreditsForFailingGrade() throws Exception {
        // Given
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(6.9);
        
        StudentDTO expectedResponse = new StudentDTO(3L, "Charlie", 1, 0);
        when(service.completeCourse(eq(3L), any(CourseCompletionRequestDTO.class)))
            .thenReturn(expectedResponse);
        
        // When / Then
        mockMvc.perform(post("/gamification/students/3/complete-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.credits").value(0));
    }
}