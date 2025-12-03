package com.example.subscription.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para o GlobalExceptionHandler.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Rickelme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>IllegalArgumentException → 400 Bad Request</li>
 *   <li>NoSuchElementException → 404 Not Found</li>
 *   <li>MethodArgumentNotValidException → 400 Validation Error</li>
 *   <li>Exception genérica → 500 Internal Server Error</li>
 * </ul>
 */
@DisplayName("GlobalExceptionHandler Unit Tests - @Rickelme")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("IllegalArgumentException Handling")
    class IllegalArgumentExceptionHandling {

        @Test
        @DisplayName("Should return 400 Bad Request with correct message")
        void shouldReturnBadRequestForIllegalArgument() {
            // Given
            String errorMessage = "Average must be between 0.0 and 10.0";
            IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(400);
            assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
            assertThat(response.getBody().get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("Should handle null message in IllegalArgumentException")
        void shouldHandleNullMessageInIllegalArgument() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException((String) null);

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isNull();
        }
    }

    @Nested
    @DisplayName("NoSuchElementException Handling")
    class NoSuchElementExceptionHandling {

        @Test
        @DisplayName("Should return 404 Not Found with correct message")
        void shouldReturnNotFoundForNoSuchElement() {
            // Given
            String errorMessage = "Student not found: 999";
            NoSuchElementException exception = new NoSuchElementException(errorMessage);

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(404);
            assertThat(response.getBody().get("error")).isEqualTo("Not Found");
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
            assertThat(response.getBody().get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("Should handle null message in NoSuchElementException")
        void shouldHandleNullMessageInNoSuchElement() {
            // Given
            NoSuchElementException exception = new NoSuchElementException((String) null);

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isNull();
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handling")
    class MethodArgumentNotValidExceptionHandling {

        @Test
        @DisplayName("Should return 400 Validation Error with field errors")
        void shouldReturnValidationErrorWithFieldErrors() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError1 = new FieldError("request", "average", "must be between 0 and 10");
            FieldError fieldError2 = new FieldError("request", "name", "must not be blank");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(400);
            assertThat(response.getBody().get("error")).isEqualTo("Validation Error");
            assertThat(response.getBody().get("message").toString())
                    .contains("average: must be between 0 and 10")
                    .contains("name: must not be blank");
            assertThat(response.getBody().get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("Should return 400 Validation Error with single field error")
        void shouldReturnValidationErrorWithSingleFieldError() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            FieldError fieldError = new FieldError("request", "email", "must be a valid email");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("email: must be a valid email");
        }

        @Test
        @DisplayName("Should handle empty field errors list")
        void shouldHandleEmptyFieldErrorsList() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling")
    class GenericExceptionHandling {

        @Test
        @DisplayName("Should return 500 Internal Server Error for generic exception")
        void shouldReturnInternalServerErrorForGenericException() {
            // Given
            Exception exception = new Exception("Something went wrong");

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(500);
            assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
            assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
            assertThat(response.getBody().get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("Should hide original message for security in generic exception")
        void shouldHideOriginalMessageForSecurity() {
            // Given
            Exception exception = new Exception("Database password: secret123");

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message"))
                    .isEqualTo("An unexpected error occurred")
                    .isNotEqualTo("Database password: secret123");
        }

        @Test
        @DisplayName("Should handle RuntimeException as generic exception")
        void shouldHandleRuntimeExceptionAsGenericException() {
            // Given
            RuntimeException exception = new RuntimeException("Unexpected runtime error");

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
        }
    }

    @Nested
    @DisplayName("Response Format Validation")
    class ResponseFormatValidation {

        @Test
        @DisplayName("Should include all required fields in response")
        void shouldIncludeAllRequiredFieldsInResponse() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("Test error");

            // When
            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(exception);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).containsKeys("timestamp", "status", "error", "message");
        }

        @Test
        @DisplayName("Should maintain consistent response structure across handlers")
        void shouldMaintainConsistentResponseStructure() {
            // Given
            IllegalArgumentException badRequestEx = new IllegalArgumentException("Bad request");
            NoSuchElementException notFoundEx = new NoSuchElementException("Not found");
            Exception genericEx = new Exception("Generic error");

            // When
            ResponseEntity<Map<String, Object>> badRequestResponse = exceptionHandler.handleIllegalArgument(badRequestEx);
            ResponseEntity<Map<String, Object>> notFoundResponse = exceptionHandler.handleNotFound(notFoundEx);
            ResponseEntity<Map<String, Object>> genericResponse = exceptionHandler.handleGenericException(genericEx);

            // Then
            assertThat(badRequestResponse.getBody()).containsKeys("timestamp", "status", "error", "message");
            assertThat(notFoundResponse.getBody()).containsKeys("timestamp", "status", "error", "message");
            assertThat(genericResponse.getBody()).containsKeys("timestamp", "status", "error", "message");
        }
    }
}

