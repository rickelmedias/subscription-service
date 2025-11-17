package com.example.subscription.domain.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("CreditStrategyFactory Tests")
class CreditStrategyFactoryTest {

    @Autowired
    private CreditStrategyFactory factory;

    @Test
    @DisplayName("Should return StandardCreditStrategy for STANDARD type")
    void shouldReturnStandardStrategyForStandardType() {
        CreditCalculationStrategy strategy = factory.getStrategy(
            CreditStrategyFactory.StrategyType.STANDARD
        );
        
        assertThat(strategy).isInstanceOf(StandardCreditStrategy.class);
        assertThat(strategy.getStrategyName()).isEqualTo("Standard Credit Strategy");
    }

    @Test
    @DisplayName("Should return PremiumCreditStrategy for PREMIUM type")
    void shouldReturnPremiumStrategyForPremiumType() {
        CreditCalculationStrategy strategy = factory.getStrategy(
            CreditStrategyFactory.StrategyType.PREMIUM
        );
        
        assertThat(strategy).isInstanceOf(PremiumCreditStrategy.class);
        assertThat(strategy.getStrategyName()).isEqualTo("Premium Credit Strategy");
    }

    @Test
    @DisplayName("Should return default strategy (Standard)")
    void shouldReturnDefaultStrategy() {
        CreditCalculationStrategy strategy = factory.getDefaultStrategy();
        
        assertThat(strategy).isInstanceOf(StandardCreditStrategy.class);
    }

    @Test
    @DisplayName("Should always return same instance for same type (singleton behavior)")
    void shouldReturnSameInstanceForSameType() {
        CreditCalculationStrategy strategy1 = factory.getStrategy(
            CreditStrategyFactory.StrategyType.STANDARD
        );
        CreditCalculationStrategy strategy2 = factory.getStrategy(
            CreditStrategyFactory.StrategyType.STANDARD
        );
        
        assertThat(strategy1).isSameAs(strategy2);
    }

    @Test
    @DisplayName("Should have both strategy types available")
    void shouldHaveBothStrategyTypesAvailable() {
        CreditCalculationStrategy standard = factory.getStrategy(
            CreditStrategyFactory.StrategyType.STANDARD
        );
        CreditCalculationStrategy premium = factory.getStrategy(
            CreditStrategyFactory.StrategyType.PREMIUM
        );
        
        assertThat(standard).isNotNull();
        assertThat(premium).isNotNull();
        assertThat(standard).isNotSameAs(premium);
    }

    @Test
    @DisplayName("Should validate StrategyType enum values")
    void shouldValidateStrategyTypeEnumValues() {
        CreditStrategyFactory.StrategyType[] types = CreditStrategyFactory.StrategyType.values();
        
        assertThat(types).hasSize(2);
        assertThat(types).contains(
            CreditStrategyFactory.StrategyType.STANDARD,
            CreditStrategyFactory.StrategyType.PREMIUM
        );
    }
}
