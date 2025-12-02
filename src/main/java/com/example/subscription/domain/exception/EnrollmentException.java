package com.example.subscription.domain.exception;

/**
 * Exceção específica para problemas de matrícula/inscrição.
 * 
 * <h2>DDD - Domain Exception:</h2>
 * <ul>
 *   <li><b>Herança</b>: Estende BusinessException (especialização)</li>
 *   <li><b>Códigos Predefinidos</b>: Constantes para erros comuns</li>
 *   <li><b>Extensibilidade</b>: Fácil adicionar novos códigos de erro</li>
 * </ul>
 * 
 * <h2>Códigos de Erro Disponíveis:</h2>
 * <ul>
 *   <li>{@link #COURSE_FULL} - Curso lotado</li>
 *   <li>{@link #PREREQUISITE_NOT_MET} - Pré-requisito não cumprido</li>
 *   <li>{@link #ALREADY_ENROLLED} - Já matriculado</li>
 *   <li>{@link #INSUFFICIENT_CREDITS} - Créditos insuficientes</li>
 * </ul>
 * 
 * <h2>Uso:</h2>
 * <pre>{@code
 * throw new EnrollmentException(EnrollmentException.COURSE_FULL, "Curso Java já está lotado");
 * }</pre>
 * 
 * @author Guilherme
 * @see BusinessException Classe pai
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