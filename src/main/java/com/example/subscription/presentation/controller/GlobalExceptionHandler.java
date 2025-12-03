package com.example.subscription.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Handler global de exceções da aplicação.
 * 
 * <h2>Clean Architecture - Presentation Layer:</h2>
 * <ul>
 *   <li><b>@RestControllerAdvice</b>: Intercepta exceções de todos os controllers</li>
 *   <li><b>Consistência</b>: Formato de erro padronizado para toda API</li>
 *   <li><b>Mapeamento</b>: Converte exceções Java em HTTP Status apropriados</li>
 * </ul>
 * 
 * <h2>Mapeamento de Exceções:</h2>
 * <table border="1">
 *   <tr><th>Exceção</th><th>HTTP Status</th></tr>
 *   <tr><td>IllegalArgumentException</td><td>400 Bad Request</td></tr>
 *   <tr><td>NoSuchElementException</td><td>404 Not Found</td></tr>
 *   <tr><td>MethodArgumentNotValidException</td><td>400 Validation Error</td></tr>
 *   <tr><td>Exception (genérica)</td><td>500 Internal Server Error</td></tr>
 * </table>
 * 
 * <h2>Formato de Resposta:</h2>
 * <pre>{@code
 * {
 *   "timestamp": "2024-01-15T10:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Average must be between 0.0 and 10.0"
 * }
 * }</pre>
 * 
 * @author Guilherme
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "Not Found",
            ex.getMessage()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation Error",
            errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred"
        );
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        
        return ResponseEntity.status(status).body(body);
    }
}