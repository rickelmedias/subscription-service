package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;
import org.springframework.stereotype.Component;

/**
 * Estratégia PREMIUM de cálculo de créditos com sistema escalonado.
 * 
 * <h2>Regras de Negócio (escalonadas):</h2>
 * <table border="1">
 *   <tr><th>Faixa de Média</th><th>Créditos</th><th>Classificação</th></tr>
 *   <tr><td>>= 9.0</td><td>5</td><td>Excelente</td></tr>
 *   <tr><td>>= 8.0</td><td>4</td><td>Muito Bom</td></tr>
 *   <tr><td>> 7.0</td><td>3</td><td>Bom</td></tr>
 *   <tr><td><= 7.0</td><td>0</td><td>Não Aprovado</td></tr>
 * </table>
 * 
 * <h2>Design Pattern - Strategy:</h2>
 * <p>Implementação alternativa que oferece mais créditos para notas altas.
 * Demonstra a extensibilidade do padrão Strategy - nova regra de negócio
 * sem modificar código existente (Open/Closed Principle).</p>
 * 
 * @author Guilherme e Rickelme
 * @see CreditCalculationStrategy Interface Strategy
 * @see StandardCreditStrategy Estratégia padrão
 */
@Component("premiumCreditStrategy")
public class PremiumCreditStrategy implements CreditCalculationStrategy {
    
    private static final double EXCELLENT_THRESHOLD = 9.0;
    private static final double VERY_GOOD_THRESHOLD = 8.0;
    private static final double GOOD_THRESHOLD = 7.0;
    
    private static final int EXCELLENT_CREDITS = 5;
    private static final int VERY_GOOD_CREDITS = 4;
    private static final int GOOD_CREDITS = 3;
    
    @Override
    public int calculateCredits(CourseAverage average) {
        double value = average.getValue();
        
        if (value >= EXCELLENT_THRESHOLD) {
            return EXCELLENT_CREDITS;
        }
        if (value >= VERY_GOOD_THRESHOLD) {
            return VERY_GOOD_CREDITS;
        }
        if (value > GOOD_THRESHOLD) {
            return GOOD_CREDITS;
        }
        return 0;
    }
    
    @Override
    public String getStrategyName() {
        return "Premium Credit Strategy";
    }
}