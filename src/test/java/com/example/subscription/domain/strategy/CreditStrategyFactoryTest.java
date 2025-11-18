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

    @Test
    @DisplayName("Should throw exception when strategy type is null")
    void shouldThrowExceptionWhenStrategyTypeIsNull() {
        assertThatThrownBy(() -> factory.getStrategy(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should test constructor initialization")
    void shouldTestConstructorInitialization() {
        // Verifica que o factory foi inicializado corretamente pelo Spring
        assertThat(factory).isNotNull();
        
        // Verifica que ambas as estratégias estão disponíveis
        CreditCalculationStrategy standard = factory.getStrategy(
            CreditStrategyFactory.StrategyType.STANDARD
        );
        CreditCalculationStrategy premium = factory.getStrategy(
            CreditStrategyFactory.StrategyType.PREMIUM
        );
        
        assertThat(standard).isNotNull();
        assertThat(premium).isNotNull();
    }

    @Test
    @DisplayName("Should test enum valueOf method")
    void shouldTestEnumValueOfMethod() {
        CreditStrategyFactory.StrategyType standard = 
            CreditStrategyFactory.StrategyType.valueOf("STANDARD");
        CreditStrategyFactory.StrategyType premium = 
            CreditStrategyFactory.StrategyType.valueOf("PREMIUM");
        
        assertThat(standard).isEqualTo(CreditStrategyFactory.StrategyType.STANDARD);
        assertThat(premium).isEqualTo(CreditStrategyFactory.StrategyType.PREMIUM);
    }
}
