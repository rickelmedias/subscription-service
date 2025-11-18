package com.example.subscription.domain.entity;

import com.example.subscription.domain.valueobject.Credits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Student Entity Tests")
class StudentTest {

    @Test
    @DisplayName("Should create student with zero credits")
    void shouldCreateStudentWithZeroCredits() {
        Student student = new Student("João");
        
        assertThat(student.getName()).isEqualTo("João");
        assertThat(student.getCompletedCourses()).isZero();
        assertThat(student.getCredits()).isZero();
    }

    @Test
    @DisplayName("Should create student with initial credits")
    void shouldCreateStudentWithInitialCredits() {
        Student student = new Student("Maria", 10);
        
        assertThat(student.getName()).isEqualTo("Maria");
        assertThat(student.getCredits()).isEqualTo(10);
        assertThat(student.getCompletedCourses()).isZero();
    }

    @Test
    @DisplayName("Should complete course and add credits when average > 7.0")
    void shouldCompleteCourseShouldAddCreditsWhenAverageAboveThreshold() {
        Student student = new Student("Ana", 2);
        
        student.completeCourse(8.5);
        
        assertThat(student.getCompletedCourses()).isEqualTo(1);
        assertThat(student.getCredits()).isEqualTo(5); // 2 + 3
    }

    @Test
    @DisplayName("Should complete course but NOT add credits when average <= 7.0")
    void shouldCompleteCourseButNotAddCreditsWhenAverageAtOrBelowThreshold() {
        Student student = new Student("Bob", 5);
        
        student.completeCourse(7.0); // Exatamente no limiar
        
        assertThat(student.getCompletedCourses()).isEqualTo(1);
        assertThat(student.getCredits()).isEqualTo(5); // Sem mudança
        
        student.completeCourse(6.9); // Abaixo do limiar
        
        assertThat(student.getCompletedCourses()).isEqualTo(2);
        assertThat(student.getCredits()).isEqualTo(5); // Ainda sem mudança
    }

    @Test
    @DisplayName("Should throw exception for invalid average")
    void shouldThrowExceptionForInvalidAverage() {
        Student student = new Student("Carlos");
        
        assertThatThrownBy(() -> student.completeCourse(-0.1))
                .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> student.completeCourse(10.1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should add credits manually")
    void shouldAddCreditsManually() {
        Student student = new Student("Diana", 5);
        
        student.addCredits(10);
        
        assertThat(student.getCredits()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should deduct credits")
    void shouldDeductCredits() {
        Student student = new Student("Eduardo", 20);
        
        student.deductCredits(5);
        
        assertThat(student.getCredits()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should throw exception when deducting more credits than available")
    void shouldThrowExceptionWhenDeductingMoreCreditsThanAvailable() {
        Student student = new Student("Fernanda", 5);
        
        assertThatThrownBy(() -> student.deductCredits(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient credits");
    }

    @Test
    @DisplayName("Should check if has enough credits")
    void shouldCheckIfHasEnoughCredits() {
        Student student = new Student("Gabriel", 10);
        
        assertThat(student.hasEnoughCredits(5)).isTrue();
        assertThat(student.hasEnoughCredits(10)).isTrue();
        assertThat(student.hasEnoughCredits(15)).isFalse();
    }

    @Test
    @DisplayName("Should be equal when IDs are equal")
    void shouldBeEqualWhenIdsAreEqual() {
        Student student1 = new Student("Helena");
        student1.setId(1L);
        
        Student student2 = new Student("Igor");
        student2.setId(1L);
        
        assertThat(student1).isEqualTo(student2); // Mesmo ID
    }

    @Test
    @DisplayName("Should complete multiple courses and accumulate credits")
    void shouldCompleteMultipleCoursesAndAccumulateCredits() {
        Student student = new Student("Julia", 0);
        
        student.completeCourse(8.0); // +3 créditos
        student.completeCourse(9.5); // +3 créditos
        student.completeCourse(7.1); // +3 créditos
        student.completeCourse(6.5); // +0 créditos (reprovado)
        
        assertThat(student.getCompletedCourses()).isEqualTo(4);
        assertThat(student.getCredits()).isEqualTo(9); // 3 aprovações × 3 créditos
    }

    @Test
    @DisplayName("Should return zero when credits is null")
    void shouldReturnZeroWhenCreditsIsNull() throws Exception {
        // Usa reflexão para criar um Student com credits null (simula estado JPA não inicializado)
        Student student = Student.builder()
                .name("Test")
                .completedCourses(0)
                .build();
        
        // Usa reflexão para definir credits como null
        java.lang.reflect.Field creditsField = Student.class.getDeclaredField("credits");
        creditsField.setAccessible(true);
        creditsField.set(student, null);
        
        // Verifica que getCredits() retorna 0 quando credits é null
        assertThat(student.getCredits()).isZero();
    }

    @Test
    @DisplayName("Should set credits using setter")
    void shouldSetCreditsUsingSetter() {
        Student student = new Student("Test");
        student.setCredits(10);
        
        assertThat(student.getCredits()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should use builder pattern")
    void shouldUseBuilderPattern() {
        Student student = Student.builder()
                .name("Builder Test")
                .completedCourses(2)
                .credits(Credits.of(5))
                .build();
        
        assertThat(student.getName()).isEqualTo("Builder Test");
        assertThat(student.getCompletedCourses()).isEqualTo(2);
        assertThat(student.getCredits()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should complete course with CourseAverage object")
    void shouldCompleteCourseWithCourseAverageObject() {
        Student student = new Student("Test", 0);
        com.example.subscription.domain.valueobject.CourseAverage average = 
            com.example.subscription.domain.valueobject.CourseAverage.of(8.5);
        
        student.completeCourse(average);
        
        assertThat(student.getCompletedCourses()).isEqualTo(1);
        assertThat(student.getCredits()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should not add credits when average equals threshold")
    void shouldNotAddCreditsWhenAverageEqualsThreshold() {
        Student student = new Student("Test", 5);
        student.completeCourse(7.0);
        
        assertThat(student.getCompletedCourses()).isEqualTo(1);
        assertThat(student.getCredits()).isEqualTo(5); // Sem mudança
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        Student student = new Student("Test Student");
        student.setId(1L);
        
        String toString = student.toString();
        assertThat(toString).isNotNull();
        assertThat(toString).contains("Test Student");
    }
}