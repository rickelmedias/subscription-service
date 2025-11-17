package com.example.subscription.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with code and message")
    void shouldCreateExceptionWithCodeAndMessage() {
        String code = "BUSINESS_ERROR";
        String message = "Business rule violated";
        
        BusinessException exception = new BusinessException(code, message);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should create exception with code, message and cause")
    void shouldCreateExceptionWithCodeMessageAndCause() {
        String code = "BUSINESS_ERROR";
        String message = "Business rule violated";
        Throwable cause = new RuntimeException("Root cause");
        
        BusinessException exception = new BusinessException(code, message, cause);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        BusinessException exception = new BusinessException("CODE", "Message");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
