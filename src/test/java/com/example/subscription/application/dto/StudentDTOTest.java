package com.example.subscription.application.dto;

import com.example.subscription.domain.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StudentDTO Tests")
class StudentDTOTest {

    @Test
    @DisplayName("Should create DTO with all fields")
    void shouldCreateDTOWithAllFields() {
        StudentDTO dto = new StudentDTO(1L, "Ana Silva", 5, 15);
        
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Ana Silva");
        assertThat(dto.getCompletedCourses()).isEqualTo(5);
        assertThat(dto.getCredits()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should create empty DTO with no-args constructor")
    void shouldCreateEmptyDTOWithNoArgsConstructor() {
        StudentDTO dto = new StudentDTO();
        
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getCompletedCourses()).isZero();
        assertThat(dto.getCredits()).isZero();
    }

    @Test
    @DisplayName("Should allow setting fields via setters")
    void shouldAllowSettingFieldsViaSetters() {
        StudentDTO dto = new StudentDTO();
        
        dto.setId(10L);
        dto.setName("Bob");
        dto.setCompletedCourses(3);
        dto.setCredits(9);
        
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getCompletedCourses()).isEqualTo(3);
        assertThat(dto.getCredits()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should convert entity to DTO")
    void shouldConvertEntityToDTO() {
        // Given
        Student student = new Student("Carlos", 5);
        student.setId(1L);
        student.completeCourse(8.5);
        student.completeCourse(9.0);
        
        // When
        StudentDTO dto = StudentDTO.fromEntity(student);
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Carlos");
        assertThat(dto.getCompletedCourses()).isEqualTo(2);
        assertThat(dto.getCredits()).isEqualTo(11); // 5 + 3 + 3
    }

    @Test
    @DisplayName("Should return null when converting null entity")
    void shouldReturnNullWhenConvertingNullEntity() {
        StudentDTO dto = StudentDTO.fromEntity(null);
        
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should convert DTO to entity")
    void shouldConvertDTOToEntity() {
        // Given
        StudentDTO dto = new StudentDTO(2L, "Diana", 3, 12);
        
        // When
        Student entity = dto.toEntity();
        
        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getName()).isEqualTo("Diana");
        assertThat(entity.getCredits()).isEqualTo(12);
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        StudentDTO dto1 = new StudentDTO(1L, "Eduardo", 2, 6);
        StudentDTO dto2 = new StudentDTO(1L, "Eduardo", 2, 6);
        StudentDTO dto3 = new StudentDTO(2L, "Fernando", 3, 9);
        
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        StudentDTO dto = new StudentDTO(1L, "Gabriel", 4, 12);
        
        String toString = dto.toString();
        
        assertThat(toString)
            .contains("1")
            .contains("Gabriel")
            .contains("4")
            .contains("12");
    }
}