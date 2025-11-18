package com.example.subscription.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para EnrollmentException - Exceção de Matrícula
 */
@DisplayName("EnrollmentException Tests")
class EnrollmentExceptionTest {

    @Test
    @DisplayName("Should create exception with code and message")
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = EnrollmentException.COURSE_FULL;
        String message = "The course has reached maximum capacity";
        
        EnrollmentException exception = new EnrollmentException(code, message);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create exception with code, message and cause")
    void shouldCreateExceptionWithCodeMessageAndCause() {
        String code = EnrollmentException.PREREQUISITE_NOT_MET;
        String message = "Student does not meet prerequisites";
        Throwable cause = new IllegalStateException("Missing required course");
        
        EnrollmentException exception = new EnrollmentException(code, message, cause);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should extend BusinessException")
    void shouldExtendBusinessException() {
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.ALREADY_ENROLLED, 
            "Student already enrolled"
        );
        
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have COURSE_FULL error code constant")
    void shouldHaveCourseFullErrorCode() {
        assertThat(EnrollmentException.COURSE_FULL).isEqualTo("COURSE_FULL");
    }

    @Test
    @DisplayName("Should have PREREQUISITE_NOT_MET error code constant")
    void shouldHavePrerequisiteNotMetErrorCode() {
        assertThat(EnrollmentException.PREREQUISITE_NOT_MET).isEqualTo("PREREQUISITE_NOT_MET");
    }

    @Test
    @DisplayName("Should have ALREADY_ENROLLED error code constant")
    void shouldHaveAlreadyEnrolledErrorCode() {
        assertThat(EnrollmentException.ALREADY_ENROLLED).isEqualTo("ALREADY_ENROLLED");
    }

    @Test
    @DisplayName("Should have INSUFFICIENT_CREDITS error code constant")
    void shouldHaveInsufficientCreditsErrorCode() {
        assertThat(EnrollmentException.INSUFFICIENT_CREDITS).isEqualTo("INSUFFICIENT_CREDITS");
    }

    @Test
    @DisplayName("Should use COURSE_FULL in exception")
    void shouldUseCourseFull() {
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.COURSE_FULL,
            "Course capacity reached"
        );
        
        assertThat(exception.getCode()).isEqualTo("COURSE_FULL");
    }

    @Test
    @DisplayName("Should use PREREQUISITE_NOT_MET in exception")
    void shouldUsePrerequisiteNotMet() {
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.PREREQUISITE_NOT_MET,
            "Missing required courses"
        );
        
        assertThat(exception.getCode()).isEqualTo("PREREQUISITE_NOT_MET");
    }

    @Test
    @DisplayName("Should use ALREADY_ENROLLED in exception")
    void shouldUseAlreadyEnrolled() {
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.ALREADY_ENROLLED,
            "Student is already enrolled in this course"
        );
        
        assertThat(exception.getCode()).isEqualTo("ALREADY_ENROLLED");
    }

    @Test
    @DisplayName("Should use INSUFFICIENT_CREDITS in exception")
    void shouldUseInsufficientCredits() {
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.INSUFFICIENT_CREDITS,
            "Not enough credits to enroll"
        );
        
        assertThat(exception.getCode()).isEqualTo("INSUFFICIENT_CREDITS");
    }

    @Test
    @DisplayName("Should create enrollment exception with custom code")
    void shouldCreateEnrollmentExceptionWithCustomCode() {
        String customCode = "CUSTOM_ENROLLMENT_ERROR";
        String message = "Custom enrollment error occurred";
        
        EnrollmentException exception = new EnrollmentException(customCode, message);
        
        assertThat(exception.getCode()).isEqualTo(customCode);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should preserve stack trace with cause")
    void shouldPreserveStackTraceWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid student ID");
        EnrollmentException exception = new EnrollmentException(
            EnrollmentException.PREREQUISITE_NOT_MET,
            "Cannot verify prerequisites",
            cause
        );
        
        assertThat(exception.getStackTrace()).isNotEmpty();
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Invalid student ID");
    }

    @Test
    @DisplayName("Should verify all error codes are unique")
    void shouldVerifyAllErrorCodesAreUnique() {
        assertThat(EnrollmentException.COURSE_FULL)
                .isNotEqualTo(EnrollmentException.PREREQUISITE_NOT_MET)
                .isNotEqualTo(EnrollmentException.ALREADY_ENROLLED)
                .isNotEqualTo(EnrollmentException.INSUFFICIENT_CREDITS);
        
        assertThat(EnrollmentException.PREREQUISITE_NOT_MET)
                .isNotEqualTo(EnrollmentException.ALREADY_ENROLLED)
                .isNotEqualTo(EnrollmentException.INSUFFICIENT_CREDITS);
        
        assertThat(EnrollmentException.ALREADY_ENROLLED)
                .isNotEqualTo(EnrollmentException.INSUFFICIENT_CREDITS);
    }
}