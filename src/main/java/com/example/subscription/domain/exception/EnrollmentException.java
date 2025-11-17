
package com.example.subscription.domain.exception;

/**
 * Exceção específica para problemas de matrícula/inscrição.
 * Exemplo de uso: curso lotado, pré-requisitos não cumpridos, etc.
 * 
 * Guilherme
 */
public class EnrollmentException extends BusinessException {
    
    public EnrollmentException(String code, String message) {
        super(code, message);
    }
    
    public EnrollmentException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    // Códigos de erro comuns
    public static final String COURSE_FULL = "COURSE_FULL";
    public static final String PREREQUISITE_NOT_MET = "PREREQUISITE_NOT_MET";
    public static final String ALREADY_ENROLLED = "ALREADY_ENROLLED";
    public static final String INSUFFICIENT_CREDITS = "INSUFFICIENT_CREDITS";
}