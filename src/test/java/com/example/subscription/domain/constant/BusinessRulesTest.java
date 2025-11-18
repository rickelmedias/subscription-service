package com.example.subscription.domain.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para BusinessRules - Constantes de Regras de Negócio
 * 
 * Objetivo: Garantir que as constantes estejam corretas e que a classe
 * não possa ser instanciada (padrão Utility Class)
 */
@DisplayName("BusinessRules Constants Tests")
class BusinessRulesTest {

    @Test
    @DisplayName("Should have correct CREDITS_PER_APPROVED_COURSE value")
    void shouldHaveCorrectCreditsPerApprovedCourse() {
        assertThat(BusinessRules.CREDITS_PER_APPROVED_COURSE)
                .isEqualTo(3);
    }

    @Test
    @DisplayName("Should have correct PASSING_GRADE_THRESHOLD value")
    void shouldHaveCorrectPassingGradeThreshold() {
        assertThat(BusinessRules.PASSING_GRADE_THRESHOLD)
                .isEqualTo(7.0);
    }

    @Test
    @DisplayName("Should have correct MIN_GRADE value")
    void shouldHaveCorrectMinGrade() {
        assertThat(BusinessRules.MIN_GRADE)
                .isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should have correct MAX_GRADE value")
    void shouldHaveCorrectMaxGrade() {
        assertThat(BusinessRules.MAX_GRADE)
                .isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should prevent instantiation of utility class")
    void shouldPreventInstantiation() throws NoSuchMethodException {
        // Obtém o construtor privado
        Constructor<BusinessRules> constructor = BusinessRules.class.getDeclaredConstructor();
        
        // Torna o construtor acessível
        constructor.setAccessible(true);
        
        // Tenta instanciar e verifica se lança UnsupportedOperationException
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("Utility class cannot be instantiated");
    }

    @Test
    @DisplayName("Should verify all constants are public static final")
    void shouldVerifyConstantsArePublicStaticFinal() throws NoSuchFieldException {
        // Verifica CREDITS_PER_APPROVED_COURSE
        var creditsField = BusinessRules.class.getDeclaredField("CREDITS_PER_APPROVED_COURSE");
        assertThat(java.lang.reflect.Modifier.isPublic(creditsField.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isStatic(creditsField.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isFinal(creditsField.getModifiers())).isTrue();
        
        // Verifica PASSING_GRADE_THRESHOLD
        var thresholdField = BusinessRules.class.getDeclaredField("PASSING_GRADE_THRESHOLD");
        assertThat(java.lang.reflect.Modifier.isPublic(thresholdField.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isStatic(thresholdField.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isFinal(thresholdField.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("Should validate business rule consistency")
    void shouldValidateBusinessRuleConsistency() {
        // Verifica que MIN_GRADE < PASSING_GRADE_THRESHOLD < MAX_GRADE
        assertThat(BusinessRules.MIN_GRADE)
                .isLessThan(BusinessRules.PASSING_GRADE_THRESHOLD);
        
        assertThat(BusinessRules.PASSING_GRADE_THRESHOLD)
                .isLessThan(BusinessRules.MAX_GRADE);
        
        // Verifica que créditos é positivo
        assertThat(BusinessRules.CREDITS_PER_APPROVED_COURSE)
                .isPositive();
    }
}