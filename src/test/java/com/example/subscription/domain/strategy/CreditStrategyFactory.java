package com.example.subscription.domain.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Fábrica para obter a estratégia correta de cálculo de créditos.
 * Utiliza o padrão Factory Method/Strategy com injeção de dependência do Spring.
 */
@Component
public class CreditStrategyFactory {

    private final Map<String, CreditCalculationStrategy> strategies;

    // Enum para tipos de estratégia
    public enum StrategyType {
        STANDARD,
        PREMIUM
    }

    /**
     * O Spring injeta todas as implementações de CreditCalculationStrategy em um Map,
     * onde a chave é o nome do bean (o valor do @Component).
     */
    @Autowired
    public CreditStrategyFactory(Map<String, CreditCalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Obtém a estratégia com base no tipo.
     * @param type o tipo de estratégia desejada
     * @return a estratégia de cálculo de créditos
     * @throws NoSuchElementException se o tipo for inválido ou a estratégia não estiver no contexto
     */
    public CreditCalculationStrategy getStrategy(StrategyType type) {
        // Converte o nome do enum para o nome do bean esperado (ex: STANDARD -> standardCreditStrategy)
        String beanName = type.name().toLowerCase() + "CreditStrategy";
        
        CreditCalculationStrategy strategy = strategies.get(beanName);
        
        if (strategy == null) {
            throw new NoSuchElementException("Strategy not found for type: " + type);
        }
        return strategy;
    }

    /**
     * Retorna a estratégia padrão.
     */
    public CreditCalculationStrategy getDefaultStrategy() {
        return getStrategy(StrategyType.STANDARD);
    }
}