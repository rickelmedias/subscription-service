package com.example.subscription.domain.exception;

/**
 * Exceção base para regras de negócio do domínio.
 * 
 * <h2>DDD - Domain Exception:</h2>
 * <ul>
 *   <li><b>RuntimeException</b>: Não força try-catch (unchecked)</li>
 *   <li><b>Código de Erro</b>: Identificador único para cada tipo de erro</li>
 *   <li><b>Extensível</b>: Base para exceções específicas do domínio</li>
 * </ul>
 * 
 * <h2>Subclasses:</h2>
 * <ul>
 *   <li>{@link EnrollmentException} - Erros de matrícula/inscrição</li>
 * </ul>
 * 
 * <h2>Uso:</h2>
 * <pre>{@code
 * throw new BusinessException("INSUFFICIENT_BALANCE", "Saldo insuficiente para operação");
 * }</pre>
 * 
 * @author Rickelme
 * @see EnrollmentException Exceção específica de matrícula
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}