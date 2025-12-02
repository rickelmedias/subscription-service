package com.example.subscription.domain.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory para criar e gerenciar estratégias de cálculo de créditos.
 * 
 * <h2>Design Patterns Combinados:</h2>
 * <ul>
 *   <li><b>Factory Pattern</b>: Centraliza criação de objetos Strategy</li>
 *   <li><b>Strategy Pattern</b>: Permite trocar algoritmos em runtime</li>
 *   <li><b>Registry Pattern</b>: Mantém mapa de estratégias disponíveis</li>
 * </ul>
 * 
 * <h2>Integração com Spring:</h2>
 * <ul>
 *   <li>Recebe estratégias via Dependency Injection</li>
 *   <li>Estratégias são Singletons gerenciados pelo Spring</li>
 *   <li>Fácil de adicionar novas estratégias</li>
 * </ul>
 * 
 * <h2>Uso:</h2>
 * <pre>{@code
 * CreditCalculationStrategy strategy = factory.getStrategy(StrategyType.PREMIUM);
 * int credits = strategy.calculateCredits(average);
 * }</pre>
 * 
 * @author Rickelme
 * @see CreditCalculationStrategy Interface das estratégias
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
        return strategies.get(type.name());
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