package com.example.subscription.domain.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BusinessRules Constants Tests")
class BusinessRulesTest {

    @Test
    @DisplayName("Should have correct credits per approved course")
    void shouldHaveCorrectCreditsPerApprovedCourse() {
        assertThat(BusinessRules.CREDITS_PER_APPROVED_COURSE).isEqualTo(3);
    }

    @Test
    @DisplayName("Should have correct passing grade threshold")
    void shouldHaveCorrectPassingGradeThreshold() {
        assertThat(BusinessRules.PASSING_GRADE_THRESHOLD).isEqualTo(7.0);
    }

    @Test
    @DisplayName("Should have correct min grade")
    void shouldHaveCorrectMinGrade() {
        assertThat(BusinessRules.MIN_GRADE).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should have correct max grade")
    void shouldHaveCorrectMaxGrade() {
        assertThat(BusinessRules.MAX_GRADE).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should not be instantiable (utility class)")
    void shouldNotBeInstantiable() throws NoSuchMethodException {
        Constructor<BusinessRules> constructor = BusinessRules.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(UnsupportedOperationException.class)
            .cause()
            .hasMessage("Utility class cannot be instantiated");
    }

    @Test
    @DisplayName("Should validate business logic consistency")
    void shouldValidateBusinessLogicConsistency() {
        // Threshold deve estar entre min e max
        assertThat(BusinessRules.PASSING_GRADE_THRESHOLD)
            .isGreaterThanOrEqualTo(BusinessRules.MIN_GRADE)
            .isLessThanOrEqualTo(BusinessRules.MAX_GRADE);
        
        // Min deve ser menor que Max
        assertThat(BusinessRules.MIN_GRADE)
            .isLessThan(BusinessRules.MAX_GRADE);
        
        // Créditos devem ser positivos
        assertThat(BusinessRules.CREDITS_PER_APPROVED_COURSE)
            .isPositive();
    }
}