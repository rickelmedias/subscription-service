package com.example.subscription.application.service;

import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Student Service Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should return a list of all students as DTOs")
    void whenGetAllStudents_shouldReturnStudentDTOList() {
        // Arrange
        Student student1 = new Student("Ana");
        student1.setId(1L);
        Student student2 = new Student("Bruno");
        student2.setId(2L);

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        // Act
        List<StudentDTO> result = studentService.getAllStudents();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Ana");
        assertThat(result.get(1).getName()).isEqualTo("Bruno");
    }
    
    @Test
    @DisplayName("Should return empty list when no students exist")
    void whenGetAllStudents_shouldReturnEmptyList() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<StudentDTO> result = studentService.getAllStudents();

        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return StudentDTO when student is found by ID")
    void whenGetStudentById_shouldReturnStudentDTO() {
        // Arrange
        Long studentId = 5L;
        Student student = new Student("Carla");
        student.setId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        StudentDTO result = studentService.getStudentById(studentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(studentId);
        assertThat(result.getName()).isEqualTo("Carla");
    }
    
    @Test
    @DisplayName("Should throw NoSuchElementException when student is NOT found by ID")
    void whenGetStudentById_shouldThrowExceptionIfNotFound() {
        // Arrange
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> studentService.getStudentById(studentId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Student not found: " + studentId);
    }
}