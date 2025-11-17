package com.example.subscription.presentation.controller;

import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.application.service.StudentService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class) // Testa apenas a camada Web para este Controller
@DisplayName("Student Controller Unit Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @MockBean // Cria um Mock do Service no contexto do Spring
    private StudentService studentService;

    @Test
    @DisplayName("GET /students should return list of students")
    void whenGetStudents_shouldReturnStudentList() throws Exception {
        // Arrange
        StudentDTO student = new StudentDTO(1L, "Test User", 0, 0);
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        // Act & Assert
        mockMvc.perform(get("/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Verifica se a lista tem 1 elemento
                .andExpect(jsonPath("$[0].name", is("Test User"))); // Verifica o nome
    }
}