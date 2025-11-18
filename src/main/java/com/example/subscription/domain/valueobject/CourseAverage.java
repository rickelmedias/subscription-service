package com.example.subscription.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object que representa a média de um curso.
 * Encapsula validações e comportamentos relacionados à média.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseAverage implements Serializable, Comparable<CourseAverage> {

    private static final long serialVersionUID = 1L;
    private static final double MIN_AVERAGE = 0.0;
    private static final double MAX_AVERAGE = 10.0;
    private static final int SCALE = 2;

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
            .setScale(SCALE, RoundingMode.HALF_UP)
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
        return String.format("%." + SCALE + "f", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseAverage)) return false;
        CourseAverage that = (CourseAverage) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Enum para nível de performance
     */
    public enum PerformanceLevel {
        EXCELLENT(9.0),
        VERY_GOOD(8.0),
        GOOD(7.0),
        AVERAGE(6.0),
        BELOW_AVERAGE(0.0);

        private final double minThreshold;

        PerformanceLevel(double minThreshold) {
            this.minThreshold = minThreshold;
        }

        /**
         * Retorna o nível de performance baseado no valor da média.
         * Complexidade mínima usando estrutura simples.
         */
        public static PerformanceLevel fromValue(double value) {
            if (value >= 9.0) return EXCELLENT;
            if (value >= 8.0) return VERY_GOOD;
            if (value > 7.0) return GOOD;
            if (value >= 6.0) return AVERAGE;
            return BELOW_AVERAGE;
        }
    }
}