package com.example.subscription.domain.constant;

/**
 * Constantes de regras de negócio.
 * Centraliza valores que definem o comportamento do domínio.
 * 
 * Princípio: Don't Repeat Yourself (DRY)
 */
public final class BusinessRules {
    
    // Previne instanciação
    private BusinessRules() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Quantidade de créditos ganhos ao completar um curso com nota aprovada
     */
    public static final int CREDITS_PER_APPROVED_COURSE = 3;
    
    /**
     * Nota mínima para ganhar créditos (exclusivo - maior que 7.0)
     */
    public static final double PASSING_GRADE_THRESHOLD = 7.0;
    
    /**
     * Nota mínima possível
     */
    public static final double MIN_GRADE = 0.0;
    
    /**
     * Nota máxima possível
     */
    public static final double MAX_GRADE = 10.0;
}