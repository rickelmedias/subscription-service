package com.example.subscription.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CourseAverage Value Object Tests")
class CourseAverageTest {

    @Test
    @DisplayName("Should create average with valid value")
    void shouldCreateAverageWithValidValue() {
        CourseAverage average = CourseAverage.of(8.5);
        assertThat(average.getValue()).isEqualTo(8.5);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, -1.0, 10.1, 11.0, 100.0})
    @DisplayName("Should throw exception for invalid averages")
    void shouldThrowExceptionForInvalidAverages(double invalidValue) {
        assertThatThrownBy(() -> CourseAverage.of(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Average must be a value between 0.0 and 10.0.");
    }

    @Test
    @DisplayName("Should throw exception for negative values (left side of OR)")
    void shouldThrowExceptionForNegativeValues() {
        assertThatThrownBy(() -> CourseAverage.of(-1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Average must be a value between 0.0 and 10.0.");
    }

    @Test
    @DisplayName("Should throw exception for values above 10.0 (right side of OR)")
    void shouldThrowExceptionForValuesAboveMax() {
        assertThatThrownBy(() -> CourseAverage.of(10.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Average must be a value between 0.0 and 10.0.");
    }

    @Test
    @DisplayName("Should test boundary values 0.0 and 10.0")
    void shouldTestBoundaryValues() {
        // Test minimum boundary
        CourseAverage min = CourseAverage.of(0.0);
        assertThat(min.getValue()).isEqualTo(0.0);
        
        // Test maximum boundary
        CourseAverage max = CourseAverage.of(10.0);
        assertThat(max.getValue()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should check if average is above threshold")
    void shouldCheckIfAverageIsAboveThreshold() {
        CourseAverage average = CourseAverage.of(8.0);
        
        assertThat(average.isAbove(7.0)).isTrue();
        assertThat(average.isAbove(8.0)).isFalse(); // Não é maior que 8.0
        assertThat(average.isAbove(9.0)).isFalse();
    }

    @Test
    @DisplayName("Should check if average is below threshold")
    void shouldCheckIfAverageIsBelowThreshold() {
        CourseAverage average = CourseAverage.of(6.5);
        
        assertThat(average.isBelow(7.0)).isTrue();
        assertThat(average.isBelow(6.5)).isFalse();
        assertThat(average.isBelow(6.0)).isFalse();
    }

    @Test
    @DisplayName("Should identify performance levels")
    void shouldIdentifyPerformanceLevels() {
        assertThat(CourseAverage.of(9.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
        
        assertThat(CourseAverage.of(8.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
        
        assertThat(CourseAverage.of(7.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
        
        assertThat(CourseAverage.of(6.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        
        assertThat(CourseAverage.of(5.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
    }

    @Test
    @DisplayName("Should round to two decimals")
    void shouldRoundToTwoDecimals() {
        CourseAverage average = CourseAverage.of(8.456);
        assertThat(average.getValue()).isEqualTo(8.46);
    }

    @Test
    @DisplayName("Should be comparable")
    void shouldBeComparable() {
        CourseAverage avg1 = CourseAverage.of(7.5);
        CourseAverage avg2 = CourseAverage.of(8.5);
        CourseAverage avg3 = CourseAverage.of(7.5);
        
        assertThat(avg1.compareTo(avg2)).isNegative();
        assertThat(avg2.compareTo(avg1)).isPositive();
        assertThat(avg1.compareTo(avg3)).isZero();
    }

    @Test
    @DisplayName("Should check if average is exactly equal to threshold")
    void shouldCheckIfAverageIsExactlyEqualToThreshold() {
        CourseAverage average = CourseAverage.of(7.0);
        
        assertThat(average.isExactly(7.0)).isTrue();
        assertThat(average.isExactly(7.1)).isFalse();
        assertThat(average.isExactly(6.9)).isFalse();
    }

    @Test
    @DisplayName("Should return formatted string representation")
    void shouldReturnFormattedStringRepresentation() {
        CourseAverage average = CourseAverage.of(8.456);
        assertThat(average.toString()).isEqualTo("8.46");
        
        CourseAverage average2 = CourseAverage.of(7.0);
        assertThat(average2.toString()).isEqualTo("7.00");
    }

    @Test
    @DisplayName("Should test all performance level boundaries")
    void shouldTestAllPerformanceLevelBoundaries() {
        // Test boundaries
        assertThat(CourseAverage.of(9.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
        
        assertThat(CourseAverage.of(8.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
        
        assertThat(CourseAverage.of(7.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        
        assertThat(CourseAverage.of(7.01).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
        
        assertThat(CourseAverage.of(6.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        
        assertThat(CourseAverage.of(5.99).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
    }

    @Test
    @DisplayName("Should test all performance level edge cases")
    void shouldTestAllPerformanceLevelEdgeCases() {
        // Test exact boundaries
        assertThat(CourseAverage.of(10.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
        
        assertThat(CourseAverage.of(9.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
        
        assertThat(CourseAverage.of(8.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
        
        assertThat(CourseAverage.of(7.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
        
        assertThat(CourseAverage.of(6.5).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        
        assertThat(CourseAverage.of(0.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
        
        assertThat(CourseAverage.of(5.0).getPerformanceLevel())
                .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
    }

    @Test
    @DisplayName("Should test rounding edge cases")
    void shouldTestRoundingEdgeCases() {
        // Test rounding up
        assertThat(CourseAverage.of(8.455).getValue()).isEqualTo(8.46);
        assertThat(CourseAverage.of(8.445).getValue()).isEqualTo(8.45);
        
        // Test rounding down
        assertThat(CourseAverage.of(7.234).getValue()).isEqualTo(7.23);
        assertThat(CourseAverage.of(7.235).getValue()).isEqualTo(7.24);
        
        // Test exact values
        assertThat(CourseAverage.of(7.0).getValue()).isEqualTo(7.0);
        assertThat(CourseAverage.of(10.0).getValue()).isEqualTo(10.0);
        assertThat(CourseAverage.of(0.0).getValue()).isEqualTo(0.0);
    }

    @Nested
    @DisplayName("PerformanceLevel Enum Tests")
    class PerformanceLevelTests {

        @Test
        @DisplayName("Should return EXCELLENT for values >= 9.0")
        void shouldReturnExcellentForHighValues() {
            assertThat(CourseAverage.PerformanceLevel.fromValue(9.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(9.5))
                    .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(10.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
        }

        @Test
        @DisplayName("Should return VERY_GOOD for values >= 8.0 and < 9.0")
        void shouldReturnVeryGoodForGoodValues() {
            assertThat(CourseAverage.PerformanceLevel.fromValue(8.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(8.5))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(8.99))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
        }

        @Test
        @DisplayName("Should return GOOD for values > 7.0 and < 8.0")
        void shouldReturnGoodForAcceptableValues() {
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.01))
                    .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.5))
                    .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.99))
                    .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
        }

        @Test
        @DisplayName("Should return AVERAGE for values >= 6.0 and <= 7.0")
        void shouldReturnAverageForMediumValues() {
            assertThat(CourseAverage.PerformanceLevel.fromValue(6.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(6.5))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        }

        @Test
        @DisplayName("Should return BELOW_AVERAGE for values < 6.0")
        void shouldReturnBelowAverageForLowValues() {
            assertThat(CourseAverage.PerformanceLevel.fromValue(0.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(3.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(5.99))
                    .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
        }

        @Test
        @DisplayName("Should test exact boundary values for all levels")
        void shouldTestExactBoundaryValues() {
            // Teste exato dos limiares
            assertThat(CourseAverage.PerformanceLevel.fromValue(9.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(8.99999))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(8.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.99999))
                    .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(7.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
            
            // CORREÇÃO AQUI: 
            // 6.99999 ainda é >= 6.0, então é AVERAGE.
            // Para ser BELOW_AVERAGE, deve ser < 6.0 (ex: 5.99999).
            assertThat(CourseAverage.PerformanceLevel.fromValue(5.99999)) 
                    .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.fromValue(6.0))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
        }

        @Test
        @DisplayName("Should verify enum values exist")
        void shouldVerifyEnumValuesExist() {
            assertThat(CourseAverage.PerformanceLevel.values())
                    .hasSize(5)
                    .contains(
                    CourseAverage.PerformanceLevel.EXCELLENT,
                    CourseAverage.PerformanceLevel.VERY_GOOD,
                    CourseAverage.PerformanceLevel.GOOD,
                    CourseAverage.PerformanceLevel.AVERAGE,
                    CourseAverage.PerformanceLevel.BELOW_AVERAGE
                    );
        }

        @Test
        @DisplayName("Should convert enum to string")
        void shouldConvertEnumToString() {
            assertThat(CourseAverage.PerformanceLevel.EXCELLENT.toString())
                    .isEqualTo("EXCELLENT");
            
            assertThat(CourseAverage.PerformanceLevel.VERY_GOOD.toString())
                    .isEqualTo("VERY_GOOD");
            
            assertThat(CourseAverage.PerformanceLevel.GOOD.toString())
                    .isEqualTo("GOOD");
            
            assertThat(CourseAverage.PerformanceLevel.AVERAGE.toString())
                    .isEqualTo("AVERAGE");
            
            assertThat(CourseAverage.PerformanceLevel.BELOW_AVERAGE.toString())
                    .isEqualTo("BELOW_AVERAGE");
        }

        @Test
        @DisplayName("Should get enum by valueOf")
        void shouldGetEnumByValueOf() {
            assertThat(CourseAverage.PerformanceLevel.valueOf("EXCELLENT"))
                    .isEqualTo(CourseAverage.PerformanceLevel.EXCELLENT);
            
            assertThat(CourseAverage.PerformanceLevel.valueOf("VERY_GOOD"))
                    .isEqualTo(CourseAverage.PerformanceLevel.VERY_GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.valueOf("GOOD"))
                    .isEqualTo(CourseAverage.PerformanceLevel.GOOD);
            
            assertThat(CourseAverage.PerformanceLevel.valueOf("AVERAGE"))
                    .isEqualTo(CourseAverage.PerformanceLevel.AVERAGE);
            
            assertThat(CourseAverage.PerformanceLevel.valueOf("BELOW_AVERAGE"))
                    .isEqualTo(CourseAverage.PerformanceLevel.BELOW_AVERAGE);
        }
    }
}