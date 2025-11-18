package com.example.subscription.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Credits Value Object Tests")
class CreditsTest {

    @Test
    @DisplayName("Should create credits with valid amount")
    void shouldCreateCreditsWithValidAmount() {
        Credits credits = Credits.of(10);
        assertThat(credits.getAmount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should create zero credits")
    void shouldCreateZeroCredits() {
        Credits credits = Credits.zero();
        assertThat(credits.getAmount()).isZero();
        assertThat(credits.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception for negative credits")
    void shouldThrowExceptionForNegativeCredits() {
        assertThatThrownBy(() -> Credits.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credits cannot be negative");
    }

    @Test
    @DisplayName("Should add credits immutably")
    void shouldAddCreditsImmutably() {
        Credits original = Credits.of(5);
        Credits result = original.add(3);
        
        assertThat(original.getAmount()).isEqualTo(5); // Original não muda
        assertThat(result.getAmount()).isEqualTo(8);   // Novo objeto
    }

    @Test
    @DisplayName("Should subtract credits immutably")
    void shouldSubtractCreditsImmutably() {
        Credits original = Credits.of(10);
        Credits result = original.subtract(3);
        
        assertThat(original.getAmount()).isEqualTo(10);
        assertThat(result.getAmount()).isEqualTo(7);
    }

    @Test
    @DisplayName("Should throw exception when subtracting more than available")
    void shouldThrowExceptionWhenSubtractingMoreThanAvailable() {
        Credits credits = Credits.of(5);
        
        assertThatThrownBy(() -> credits.subtract(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient credits");
    }

    @Test
    @DisplayName("Should check if has at least required amount")
    void shouldCheckIfHasAtLeastRequiredAmount() {
        Credits credits = Credits.of(10);
        
        assertThat(credits.hasAtLeast(5)).isTrue();
        assertThat(credits.hasAtLeast(10)).isTrue();
        assertThat(credits.hasAtLeast(15)).isFalse();
    }

    @Test
    @DisplayName("Should be equal when amounts are equal")
    void shouldBeEqualWhenAmountsAreEqual() {
        Credits credits1 = Credits.of(10);
        Credits credits2 = Credits.of(10);
        
        assertThat(credits1).isEqualTo(credits2);
        assertThat(credits1.hashCode()).isEqualTo(credits2.hashCode());
    }

    @Test
    @DisplayName("Should return formatted string representation")
    void shouldReturnFormattedStringRepresentation() {
        Credits credits = Credits.of(10);
        assertThat(credits.toString()).isEqualTo("10 credits");
        
        Credits zeroCredits = Credits.zero();
        assertThat(zeroCredits.toString()).isEqualTo("0 credits");
    }

    @Test
    @DisplayName("Should check if isEmpty for non-zero credits")
    void shouldCheckIfIsEmptyForNonZeroCredits() {
        Credits credits = Credits.of(5);
        assertThat(credits.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Should test subtract with exact amount")
    void shouldTestSubtractWithExactAmount() {
        Credits credits = Credits.of(10);
        Credits result = credits.subtract(10);
        
        assertThat(result.getAmount()).isZero();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should test add with zero")
    void shouldTestAddWithZero() {
        Credits credits = Credits.of(5);
        Credits result = credits.add(0);
        
        assertThat(result.getAmount()).isEqualTo(5);
        assertThat(result).isEqualTo(credits);
    }

    @Test
    @DisplayName("Should test hasAtLeast edge cases")
    void shouldTestHasAtLeastEdgeCases() {
        Credits credits = Credits.of(10);
        
        assertThat(credits.hasAtLeast(0)).isTrue();
        assertThat(credits.hasAtLeast(1)).isTrue();
        assertThat(credits.hasAtLeast(9)).isTrue();
        assertThat(credits.hasAtLeast(10)).isTrue();
        assertThat(credits.hasAtLeast(11)).isFalse();
    }
}