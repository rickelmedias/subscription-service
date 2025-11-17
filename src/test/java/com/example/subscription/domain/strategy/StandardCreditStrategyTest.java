package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StandardCreditStrategy Tests")
class StandardCreditStrategyTest {

    private final StandardCreditStrategy strategy = new StandardCreditStrategy();

    @Test
    @DisplayName("Should return 3 credits when average > 7.0")
    void shouldReturn3CreditsWhenAverageAboveThreshold() {
        CourseAverage average = CourseAverage.of(7.1);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return 0 credits when average = 7.0")
    void shouldReturn0CreditsWhenAverageEqualsThreshold() {
        CourseAverage average = CourseAverage.of(7.0);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isZero();
    }

    @Test
    @DisplayName("Should return 0 credits when average < 7.0")
    void shouldReturn0CreditsWhenAverageBelowThreshold() {
        CourseAverage average = CourseAverage.of(6.9);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isZero();
    }

    @ParameterizedTest
    @CsvSource({
        "10.0, 3",
        "9.5, 3",
        "8.0, 3",
        "7.1, 3",
        "7.0, 0",
        "6.9, 0",
        "5.0, 0",
        "0.0, 0"
    })
    @DisplayName("Should calculate credits correctly for various averages")
    void shouldCalculateCreditsCorrectlyForVariousAverages(double averageValue, int expectedCredits) {
        CourseAverage average = CourseAverage.of(averageValue);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(expectedCredits);
    }

    @Test
    @DisplayName("Should have correct strategy name")
    void shouldHaveCorrectStrategyName() {
        assertThat(strategy.getStrategyName()).isEqualTo("Standard Credit Strategy");
    }
}