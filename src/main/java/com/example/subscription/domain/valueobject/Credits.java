package com.example.subscription.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Value Object que representa créditos do estudante.
 * Princípios DDD:
 * - Imutável (novos valores geram novos objetos)
 * - Auto-validável
 * - Sem identidade própria
 * - Embeddable no JPA
 * 
 * Guilherme
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Para JPA
public class Credits implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int amount;
    
    private Credits(int amount) {
        validate(amount);
        this.amount = amount;
    }
    
    /**
     * Factory method para criar créditos
     */
    public static Credits of(int amount) {
        return new Credits(amount);
    }
    
    /**
     * Factory method para créditos zero
     */
    public static Credits zero() {
        return new Credits(0);
    }
    
    /**
     * Valida que créditos nunca sejam negativos
     */
    private void validate(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Credits cannot be negative: " + amount);
        }
    }
    
    /**
     * Adiciona créditos (retorna novo objeto - imutabilidade)
     */
    public Credits add(int value) {
        return new Credits(this.amount + value);
    }
    
    /**
     * Remove créditos (retorna novo objeto - imutabilidade)
     */
    public Credits subtract(int value) {
        validateSufficientCredits(value);
        return new Credits(this.amount - value);
    }
    
    /**
     * Valida se há créditos suficientes para subtrair.
     * Extraído para reduzir complexidade ciclomática.
     */
    private void validateSufficientCredits(int value) {
        if (this.amount < value) {
            throw new IllegalArgumentException(
                String.format("Insufficient credits: has %d, needs %d", this.amount, value)
            );
        }
    }
    
    /**
     * Verifica se tem créditos suficientes
     */
    public boolean hasAtLeast(int requiredAmount) {
        return this.amount >= requiredAmount;
    }
    
    /**
     * Verifica se está zerado
     */
    public boolean isEmpty() {
        return this.amount == 0;
    }
    
    @Override
    public String toString() {
        return String.valueOf(amount) + " credits";
    }
}