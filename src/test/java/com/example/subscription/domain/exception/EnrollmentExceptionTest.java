package com.example.subscription.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EnrollmentException Tests")
class EnrollmentExceptionTest {

    @Test
    @DisplayName("Should create EnrollmentException with code and message")
    void shouldCreateEnrollmentExceptionWithCodeAndMessage() {
        String expectedCode = "COURSE_FULL";
        String expectedMessage = "O curso já está com a capacidade máxima.";

        EnrollmentException exception = new EnrollmentException(expectedCode, expectedMessage);

        assertThat(exception.getCode()).isEqualTo(expectedCode);
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should create EnrollmentException with code, message and cause")
    void shouldCreateEnrollmentExceptionWithCodeMessageAndCause() {
        String code = "PREREQUISITE_NOT_MET";
        String message = "Prerequisites not fulfilled";
        Throwable cause = new IllegalStateException("Invalid state");
        
        EnrollmentException exception = new EnrollmentException(code, message, cause);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should extend BusinessException")
    void shouldExtendBusinessException() {
        EnrollmentException exception = new EnrollmentException("CODE", "Message");
        
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have correct error codes constants")
    void shouldHaveCorrectErrorCodesConstants() {
        assertThat(EnrollmentException.COURSE_FULL).isEqualTo("COURSE_FULL");
        assertThat(EnrollmentException.PREREQUISITE_NOT_MET).isEqualTo("PREREQUISITE_NOT_MET");
        assertThat(EnrollmentException.ALREADY_ENROLLED).isEqualTo("ALREADY_ENROLLED");
        assertThat(EnrollmentException.INSUFFICIENT_CREDITS).isEqualTo("INSUFFICIENT_CREDITS");
    }

    @Test
    @DisplayName("Should create exception using constant codes")
    void shouldCreateExceptionUsingConstantCodes() {
        EnrollmentException courseFull = new EnrollmentException(
            EnrollmentException.COURSE_FULL, 
            "Course is full"
        );
        
        EnrollmentException prerequisite = new EnrollmentException(
            EnrollmentException.PREREQUISITE_NOT_MET, 
            "Prerequisites not met"
        );
        
        EnrollmentException alreadyEnrolled = new EnrollmentException(
            EnrollmentException.ALREADY_ENROLLED, 
            "Already enrolled"
        );
        
        EnrollmentException insufficientCredits = new EnrollmentException(
            EnrollmentException.INSUFFICIENT_CREDITS, 
            "Not enough credits"
        );
        
        assertThat(courseFull.getCode()).isEqualTo("COURSE_FULL");
        assertThat(prerequisite.getCode()).isEqualTo("PREREQUISITE_NOT_MET");
        assertThat(alreadyEnrolled.getCode()).isEqualTo("ALREADY_ENROLLED");
        assertThat(insufficientCredits.getCode()).isEqualTo("INSUFFICIENT_CREDITS");
    }
}