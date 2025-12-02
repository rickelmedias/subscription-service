package com.example.subscription.domain.strategy;

import com.example.subscription.domain.constant.BusinessRules;
import com.example.subscription.domain.valueobject.CourseAverage;
import org.springframework.stereotype.Component;

/**
 * Estratégia PADRÃO de cálculo de créditos.
 * 
 * <h2>Regra de Negócio:</h2>
 * <ul>
 *   <li>Média > 7.0: Ganha 3 créditos</li>
 *   <li>Média <= 7.0: Não ganha créditos</li>
 * </ul>
 * 
 * <h2>Design Pattern - Strategy:</h2>
 * <p>Implementação concreta da interface {@link CreditCalculationStrategy}.
 * Pode ser substituída por outras estratégias (ex: Premium) sem alterar
 * o código cliente.</p>
 * 
 * @author Guilherme e Rickelme
 * @see CreditCalculationStrategy Interface Strategy
 * @see PremiumCreditStrategy Estratégia alternativa
 * @see BusinessRules Constantes de regras de negócio
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
