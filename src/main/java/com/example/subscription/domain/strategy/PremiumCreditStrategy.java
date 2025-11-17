package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;
import org.springframework.stereotype.Component;

/**
 * Estratégia PREMIUM de cálculo de créditos.
 * Regra escalonada por faixa de nota:
 * - >= 9.0: 5 créditos (Excelente)
 * - >= 8.0: 4 créditos (Ótimo)
 * - > 7.0: 3 créditos (Bom)
 * - <= 7.0: 0 créditos (Não aprovado)
 * 
 * Guilherme e Rickelme
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