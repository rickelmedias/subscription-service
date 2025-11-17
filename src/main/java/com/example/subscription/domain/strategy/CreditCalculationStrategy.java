package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;

/**
 * Strategy Pattern para cálculo de créditos.
 * 
 * Princípios SOLID aplicados:
 * - Single Responsibility: Cada estratégia calcula créditos de uma forma
 * - Open/Closed: Aberto para extensão (novas estratégias), fechado para modificação
 * - Liskov Substitution: Qualquer estratégia pode substituir outra
 * - Interface Segregation: Interface mínima e focada
 * - Dependency Inversion: Dependemos da abstração, não da implementação
 * 
 * Guilherme
 */
public interface CreditCalculationStrategy {
    
    /**
     * Calcula quantos créditos o estudante deve receber baseado na média
     * 
     * @param average média obtida no curso
     * @return quantidade de créditos a serem adicionados
     */
    int calculateCredits(CourseAverage average);
    
    /**
     * Retorna o nome da estratégia (para logging/debugging)
     */
    String getStrategyName();
}