package com.example.subscription.domain.strategy;

import com.example.subscription.domain.constant.BusinessRules;
import com.example.subscription.domain.valueobject.CourseAverage;
import org.springframework.stereotype.Component;

/**
 * Estratégia PADRÃO de cálculo de créditos.
 * Regra: 3 créditos se média > 7.0, caso contrário 0
 * 
 * Guilherme e Rickelme
 */
@Component("standardCreditStrategy")
public class StandardCreditStrategy implements CreditCalculationStrategy {
    
    @Override
    public int calculateCredits(CourseAverage average) {
        if (average.isAbove(BusinessRules.PASSING_GRADE_THRESHOLD)) {
            return BusinessRules.CREDITS_PER_APPROVED_COURSE;
        }
        return 0;
    }
    
    @Override
    public String getStrategyName() {
        return "Standard Credit Strategy";
    }
    
}
