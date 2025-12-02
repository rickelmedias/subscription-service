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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("GET /students/{id} should return student by ID")
    void whenGetStudentById_shouldReturnStudent() throws Exception {
        // Arrange
        StudentDTO student = new StudentDTO(1L, "Test User", 2, 5);
        when(studentService.getStudentById(1L)).thenReturn(student);

        // Act & Assert
        mockMvc.perform(get("/students/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.completedCourses", is(2)))
                .andExpect(jsonPath("$.credits", is(5)));
    }

    @Test
    @DisplayName("GET /students/{id} should return 404 when student not found")
    void whenGetStudentByIdNotFound_shouldReturn404() throws Exception {
        // Arrange
        when(studentService.getStudentById(999L))
            .thenThrow(new java.util.NoSuchElementException("Student not found: 999"));

        // Act & Assert
        mockMvc.perform(get("/students/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /students should return empty list when no students")
    void whenGetStudentsWithNoStudents_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Teste para criação de estudante via POST.
     * @author Guilherme
     */
    @Test
    @DisplayName("POST /students should create student and return 201")
    void whenCreateStudent_shouldReturnCreatedStudent() throws Exception {
        // Arrange
        StudentDTO inputDto = new StudentDTO(null, "Novo Aluno", 0, 0);
        StudentDTO createdDto = new StudentDTO(10L, "Novo Aluno", 0, 0);
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(createdDto);

        // Act & Assert
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Novo Aluno\", \"completedCourses\": 0, \"credits\": 0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Novo Aluno")));
    }
}