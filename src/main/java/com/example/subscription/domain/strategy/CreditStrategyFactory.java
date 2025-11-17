package com.example.subscription.domain.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory para criar estratégias de cálculo de créditos.
 * 
 * Padrão Factory + Strategy:
 * - Centraliza a criação de estratégias
 * - Facilita injeção de dependências via Spring
 * - Permite trocar estratégias em runtime
 * 
 * Rickelme
 */
@Component
public class CreditStrategyFactory {
    
    private final Map<String, CreditCalculationStrategy> strategies;
    
    @Autowired
    public CreditStrategyFactory(
            StandardCreditStrategy standardStrategy,
            PremiumCreditStrategy premiumStrategy) {
        
        this.strategies = Map.of(
            StrategyType.STANDARD.name(), standardStrategy,
            StrategyType.PREMIUM.name(), premiumStrategy
        );
    }
    
    /**
     * Retorna a estratégia baseada no tipo
     */
    public CreditCalculationStrategy getStrategy(StrategyType type) {
        CreditCalculationStrategy strategy = strategies.get(type.name());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy type: " + type);
        }
        return strategy;
    }
    
    /**
     * Retorna a estratégia padrão
     */
    public CreditCalculationStrategy getDefaultStrategy() {
        return getStrategy(StrategyType.STANDARD);
    }
    
    /**
     * Enum para tipos de estratégia
     */
    public enum StrategyType {
        STANDARD,  // Estratégia padrão (atual do sistema)
        PREMIUM    // Estratégia premium (futura funcionalidade)
    }
}