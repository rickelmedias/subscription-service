package com.example.subscription.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para BusinessException - Exceção Base de Negócio
 */
@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with code and message")
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = "ERR001";
        String message = "Business rule violation";
        
        BusinessException exception = new BusinessException(code, message);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create exception with code, message and cause")
    void shouldCreateExceptionWithCodeMessageAndCause() {
        String code = "ERR002";
        String message = "Database connection failed";
        Throwable cause = new RuntimeException("Connection timeout");
        
        BusinessException exception = new BusinessException(code, message, cause);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Connection timeout");
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeARuntimeException() {
        BusinessException exception = new BusinessException("ERR003", "Test");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle null code")
    void shouldHandleNullCode() {
        BusinessException exception = new BusinessException(null, "Message without code");
        
        assertThat(exception.getCode()).isNull();
        assertThat(exception.getMessage()).isEqualTo("Message without code");
    }

    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() {
        BusinessException exception = new BusinessException("ERR004", null);
        
        assertThat(exception.getCode()).isEqualTo("ERR004");
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should handle empty code")
    void shouldHandleEmptyCode() {
        BusinessException exception = new BusinessException("", "Message with empty code");
        
        assertThat(exception.getCode()).isEmpty();
        assertThat(exception.getMessage()).isEqualTo("Message with empty code");
    }

    @Test
    @DisplayName("Should preserve exception stack trace")
    void shouldPreserveExceptionStackTrace() {
        Throwable cause = new IllegalStateException("Original error");
        BusinessException exception = new BusinessException("ERR005", "Wrapped error", cause);
        
        assertThat(exception.getStackTrace()).isNotEmpty();
        assertThat(exception.getCause().getStackTrace()).isNotEmpty();
    }

    @Test
    @DisplayName("Should create nested exceptions")
    void shouldCreateNestedException() {
        Throwable rootCause = new IllegalArgumentException("Invalid input");
        Throwable intermediateCause = new BusinessException("ERR006", "Processing failed", rootCause);
        BusinessException finalException = new BusinessException("ERR007", "Operation failed", intermediateCause);
        
        assertThat(finalException.getCause()).isEqualTo(intermediateCause);
        assertThat(finalException.getCause().getCause()).isEqualTo(rootCause);
    }
}