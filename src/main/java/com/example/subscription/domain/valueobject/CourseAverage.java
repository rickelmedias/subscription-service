package com.example.subscription.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa a média de um curso.
 * Encapsula validações e comportamentos relacionados à média.
 * 
 * Rickelme
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseAverage implements Serializable, Comparable<CourseAverage> {
    
    private static final long serialVersionUID = 1L;
    private static final double MIN_AVERAGE = 0.0;
    private static final double MAX_AVERAGE = 10.0;
    
    private double value;
    
    private CourseAverage(double value) {
        validate(value);
        this.value = roundToTwoDecimals(value);
    }
    
    /**
     * Factory method para criar média
     */
    public static CourseAverage of(double value) {
        return new CourseAverage(value);
    }
    
    /**
     * Valida se a média está no intervalo permitido
     */
    private void validate(double value) {
        if (value < MIN_AVERAGE || value > MAX_AVERAGE) {
            throw new IllegalArgumentException(
                String.format("Average must be a value between %.1f and %.1f.", MIN_AVERAGE, MAX_AVERAGE)
            );
        }
    }
    
    /**
     * Arredonda para 2 casas decimais
     */
    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    /**
     * Verifica se a média está acima do limiar (aprovação)
     */
    public boolean isAbove(double threshold) {
        return this.value > threshold;
    }
    
    /**
     * Verifica se a média está abaixo do limiar (reprovação)
     */
    public boolean isBelow(double threshold) {
        return this.value < threshold;
    }
    
    /**
     * Verifica se a média é exatamente igual ao limiar
     */
    public boolean isExactly(double threshold) {
        return Double.compare(this.value, threshold) == 0;
    }
    
    /**
     * Retorna a faixa de aproveitamento
     */
    public PerformanceLevel getPerformanceLevel() {
        return PerformanceLevel.fromValue(value);
    }
    
    @Override
    public int compareTo(CourseAverage other) {
        return Double.compare(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
    
    /**
     * Enum para nível de performance
     */
    public enum PerformanceLevel {
        EXCELLENT,      // >= 9.0
        VERY_GOOD,      // >= 8.0
        GOOD,           // > 7.0
        AVERAGE,        // >= 6.0
        BELOW_AVERAGE;  // < 6.0
        
        private static final PerformanceLevel[] LEVELS_BY_THRESHOLD = {
            EXCELLENT,    // index 0: >= 9.0
            VERY_GOOD,    // index 1: >= 8.0
            GOOD,         // index 2: > 7.0
            AVERAGE,      // index 3: >= 6.0
            BELOW_AVERAGE // index 4: < 6.0
        };
        
        /**
         * Retorna o nível de performance baseado no valor da média.
         * Reduz complexidade ciclomática usando array indexado.
         */
        public static PerformanceLevel fromValue(double value) {
            int index = findPerformanceIndex(value);
            return LEVELS_BY_THRESHOLD[index];
        }
        
        /**
         * Encontra o índice do nível de performance usando comparações ordenadas.
         * Reduz complexidade ciclomática ao mínimo necessário.
         */
        private static int findPerformanceIndex(double value) {
            if (value >= 9.0) return 0; // EXCELLENT
            if (value >= 8.0) return 1; // VERY_GOOD
            if (value > 7.0) return 2;  // GOOD
            if (value >= 6.0) return 3; // AVERAGE
            return 4; // BELOW_AVERAGE
        }
    }
}