package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;

/**
 * Interface Strategy para cálculo de créditos.
 * 
 * <h2>Design Pattern - Strategy:</h2>
 * <p>Define uma família de algoritmos de cálculo de créditos, encapsula cada um
 * e os torna intercambiáveis. Permite que o algoritmo varie independentemente
 * dos clientes que o utilizam.</p>
 * 
 * <h2>Princípios SOLID aplicados:</h2>
 * <ul>
 *   <li><b>S</b> - Single Responsibility: Cada estratégia calcula créditos de uma forma</li>
 *   <li><b>O</b> - Open/Closed: Aberto para extensão, fechado para modificação</li>
 *   <li><b>L</b> - Liskov Substitution: Qualquer estratégia pode substituir outra</li>
 *   <li><b>I</b> - Interface Segregation: Interface mínima e focada</li>
 *   <li><b>D</b> - Dependency Inversion: Clientes dependem desta abstração</li>
 * </ul>
 * 
 * <h2>Implementações disponíveis:</h2>
 * <ul>
 *   <li>{@link StandardCreditStrategy} - Regra padrão: 3 créditos se média > 7.0</li>
 *   <li>{@link PremiumCreditStrategy} - Regra escalonada por faixa de nota</li>
 * </ul>
 * 
 * @author Guilherme
 * @see CreditStrategyFactory Factory para criar estratégias
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