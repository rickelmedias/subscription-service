package com.example.subscription.domain.strategy;

import com.example.subscription.domain.valueobject.CourseAverage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PremiumCreditStrategy Tests")
class PremiumCreditStrategyTest {

    private final PremiumCreditStrategy strategy = new PremiumCreditStrategy();

    @Test
    @DisplayName("Should return 5 credits for excellent performance (>= 9.0)")
    void shouldReturn5CreditsForExcellentPerformance() {
        CourseAverage average = CourseAverage.of(9.5);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return 4 credits for very good performance (>= 8.0)")
    void shouldReturn4CreditsForVeryGoodPerformance() {
        CourseAverage average = CourseAverage.of(8.5);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(4);
    }

    @Test
    @DisplayName("Should return 3 credits for good performance (> 7.0)")
    void shouldReturn3CreditsForGoodPerformance() {
        CourseAverage average = CourseAverage.of(7.5);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return 0 credits for average <= 7.0")
    void shouldReturn0CreditsForAverageBelowThreshold() {
        CourseAverage average = CourseAverage.of(7.0);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isZero();
    }

    @ParameterizedTest
    @CsvSource({
        "10.0, 5",   // Excelente
        "9.5, 5",    // Excelente
        "9.0, 5",    // Excelente (limite)
        "8.9, 4",    // Muito bom
        "8.5, 4",    // Muito bom
        "8.0, 4",    // Muito bom (limite)
        "7.9, 3",    // Bom
        "7.5, 3",    // Bom
        "7.1, 3",    // Bom
        "7.0, 0",    // Não aprovado
        "6.9, 0",    // Não aprovado
        "5.0, 0",    // Não aprovado
        "0.0, 0"     // Não aprovado
    })
    @DisplayName("Should calculate credits correctly for various averages (Premium)")
    void shouldCalculateCreditsCorrectlyForVariousAverages(double averageValue, int expectedCredits) {
        CourseAverage average = CourseAverage.of(averageValue);
        
        int credits = strategy.calculateCredits(average);
        
        assertThat(credits).isEqualTo(expectedCredits);
    }

    @Test
    @DisplayName("Should have correct strategy name")
    void shouldHaveCorrectStrategyName() {
        assertThat(strategy.getStrategyName()).isEqualTo("Premium Credit Strategy");
    }

    @Test
    @DisplayName("Should give more credits than standard for high averages")
    void shouldGiveMoreCreditsThanStandardForHighAverages() {
        StandardCreditStrategy standardStrategy = new StandardCreditStrategy();
        PremiumCreditStrategy premiumStrategy = new PremiumCreditStrategy();
        
        CourseAverage excellent = CourseAverage.of(9.5);
        CourseAverage veryGood = CourseAverage.of(8.5);
        
        // Premium dá mais créditos
        assertThat(premiumStrategy.calculateCredits(excellent))
            .isGreaterThan(standardStrategy.calculateCredits(excellent));
        
        assertThat(premiumStrategy.calculateCredits(veryGood))
            .isGreaterThan(standardStrategy.calculateCredits(veryGood));
    }
}
