package com.example.subscription.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CourseCompletionRequestDTO Tests")
class CourseCompletionRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create DTO with valid average")
    void shouldCreateDTOWithValidAverage() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO();
        dto.setAverage(8.5);
        
        assertThat(dto.getAverage()).isEqualTo(8.5);
    }

    @Test
    @DisplayName("Should create DTO with all-args constructor")
    void shouldCreateDTOWithAllArgsConstructor() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(9.0);
        
        assertThat(dto.getAverage()).isEqualTo(9.0);
    }

    @Test
    @DisplayName("Should create empty DTO with no-args constructor")
    void shouldCreateEmptyDTOWithNoArgsConstructor() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO();
        
        assertThat(dto.getAverage()).isZero();
    }

    @Test
    @DisplayName("Should validate average within range (0.0 to 10.0)")
    void shouldValidateAverageWithinRange() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(8.5);
        
        Set<ConstraintViolation<CourseCompletionRequestDTO>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 5.0, 7.0, 10.0})
    @DisplayName("Should accept valid averages at boundaries")
    void shouldAcceptValidAveragesAtBoundaries(double average) {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(average);
        
        Set<ConstraintViolation<CourseCompletionRequestDTO>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject average below 0.0")
    void shouldRejectAverageBelowZero() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(-0.1);
        
        Set<ConstraintViolation<CourseCompletionRequestDTO>> violations = validator.validate(dto);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getMessage().contains("at least 0.0")
        );
    }

    @Test
    @DisplayName("Should reject average above 10.0")
    void shouldRejectAverageAboveTen() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(10.1);
        
        Set<ConstraintViolation<CourseCompletionRequestDTO>> violations = validator.validate(dto);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getMessage().contains("at most 10.0")
        );
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        CourseCompletionRequestDTO dto1 = new CourseCompletionRequestDTO(8.5);
        CourseCompletionRequestDTO dto2 = new CourseCompletionRequestDTO(8.5);
        CourseCompletionRequestDTO dto3 = new CourseCompletionRequestDTO(9.0);
        
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(9.5);
        
        String toString = dto.toString();
        
        assertThat(toString).contains("9.5");
    }

    @Test
    @DisplayName("Should allow modification of average")
    void shouldAllowModificationOfAverage() {
        CourseCompletionRequestDTO dto = new CourseCompletionRequestDTO(7.0);
        
        dto.setAverage(8.0);
        
        assertThat(dto.getAverage()).isEqualTo(8.0);
    }
}