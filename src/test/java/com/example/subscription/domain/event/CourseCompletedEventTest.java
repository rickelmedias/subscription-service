package com.example.subscription.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para o Domain Event CourseCompletedEvent.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Guilherme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Factory method of()</li>
 *   <li>Método deservesCertificate() - regras de certificação</li>
 *   <li>Método isMilestone() - marcos de conquista</li>
 *   <li>Imutabilidade do evento</li>
 *   <li>Record fields e serialização</li>
 * </ul>
 */
@DisplayName("CourseCompletedEvent Domain Event Tests - @Guilherme")
class CourseCompletedEventTest {

    @Nested
    @DisplayName("Factory Method - of()")
    class FactoryMethod {

        @Test
        @DisplayName("Should create event with all fields populated")
        void shouldCreateEventWithAllFieldsPopulated() {
            // When
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L,
                    "João Silva",
                    5,
                    25,
                    8.5,
                    true
            );

            // Then
            assertThat(event.studentId()).isEqualTo(1L);
            assertThat(event.studentName()).isEqualTo("João Silva");
            assertThat(event.completedCourses()).isEqualTo(5);
            assertThat(event.currentCredits()).isEqualTo(25);
            assertThat(event.courseAverage()).isEqualTo(8.5);
            assertThat(event.passed()).isTrue();
            assertThat(event.occurredAt()).isNotNull();
            assertThat(event.eventType()).isEqualTo("COURSE_COMPLETED");
        }

        @Test
        @DisplayName("Should set occurredAt to current time")
        void shouldSetOccurredAtToCurrentTime() {
            // Given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // When
            CourseCompletedEvent event = CourseCompletedEvent.of(1L, "Test", 1, 5, 7.0, true);

            // Then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(event.occurredAt())
                    .isAfter(before)
                    .isBefore(after);
        }

        @Test
        @DisplayName("Should always set eventType to COURSE_COMPLETED")
        void shouldAlwaysSetEventTypeToCourseCompleted() {
            // When
            CourseCompletedEvent event = CourseCompletedEvent.of(1L, "Test", 1, 5, 7.0, true);

            // Then
            assertThat(event.eventType()).isEqualTo("COURSE_COMPLETED");
        }

        @Test
        @DisplayName("Should create event for failed course")
        void shouldCreateEventForFailedCourse() {
            // When
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    2L,
                    "Maria",
                    3,
                    10,
                    5.5,
                    false
            );

            // Then
            assertThat(event.passed()).isFalse();
            assertThat(event.courseAverage()).isEqualTo(5.5);
        }
    }

    @Nested
    @DisplayName("deservesCertificate()")
    class DeservesCertificate {

        @Test
        @DisplayName("Should return true when passed with average >= 7.0")
        void shouldReturnTrueWhenPassedWithGoodAverage() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 1, 5, 7.0, true
            );

            // When / Then
            assertThat(event.deservesCertificate()).isTrue();
        }

        @Test
        @DisplayName("Should return true when passed with excellent average")
        void shouldReturnTrueWhenPassedWithExcellentAverage() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 1, 5, 10.0, true
            );

            // When / Then
            assertThat(event.deservesCertificate()).isTrue();
        }

        @Test
        @DisplayName("Should return false when passed but average < 7.0")
        void shouldReturnFalseWhenPassedButLowAverage() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 1, 5, 6.9, true
            );

            // When / Then
            assertThat(event.deservesCertificate()).isFalse();
        }

        @Test
        @DisplayName("Should return false when not passed regardless of average")
        void shouldReturnFalseWhenNotPassedRegardlessOfAverage() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 1, 5, 8.0, false
            );

            // When / Then
            assertThat(event.deservesCertificate()).isFalse();
        }

        @ParameterizedTest
        @DisplayName("Should evaluate certificate eligibility correctly")
        @CsvSource({
                "true, 7.0, true",    // passed, exact threshold
                "true, 7.1, true",    // passed, above threshold
                "true, 9.5, true",    // passed, excellent
                "true, 6.9, false",   // passed, below threshold
                "true, 0.0, false",   // passed, zero average
                "false, 10.0, false", // not passed, perfect average
                "false, 7.0, false",  // not passed, at threshold
                "false, 5.0, false"   // not passed, below threshold
        })
        void shouldEvaluateCertificateEligibilityCorrectly(boolean passed, double average, boolean expectedResult) {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", 1, 5, average, passed
            );

            // When / Then
            assertThat(event.deservesCertificate()).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("isMilestone()")
    class IsMilestone {

        @Test
        @DisplayName("Should return true when completedCourses is multiple of 5")
        void shouldReturnTrueWhenMultipleOf5() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 5, 25, 8.0, true
            );

            // When / Then
            assertThat(event.isMilestone()).isTrue();
        }

        @Test
        @DisplayName("Should return true for 10 completed courses")
        void shouldReturnTrueFor10Courses() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 10, 50, 8.0, true
            );

            // When / Then
            assertThat(event.isMilestone()).isTrue();
        }

        @Test
        @DisplayName("Should return false when completedCourses is not multiple of 5")
        void shouldReturnFalseWhenNotMultipleOf5() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 3, 15, 8.0, true
            );

            // When / Then
            assertThat(event.isMilestone()).isFalse();
        }

        @ParameterizedTest
        @DisplayName("Should identify milestones correctly")
        @ValueSource(ints = {5, 10, 15, 20, 25, 50, 100})
        void shouldIdentifyMilestonesCorrectly(int completedCourses) {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", completedCourses, completedCourses * 5, 8.0, true
            );

            // When / Then
            assertThat(event.isMilestone()).isTrue();
        }

        @ParameterizedTest
        @DisplayName("Should not identify non-milestones as milestones")
        @ValueSource(ints = {1, 2, 3, 4, 6, 7, 8, 9, 11, 13, 17, 23})
        void shouldNotIdentifyNonMilestonesAsMilestones(int completedCourses) {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", completedCourses, completedCourses * 5, 8.0, true
            );

            // When / Then
            assertThat(event.isMilestone()).isFalse();
        }

        @Test
        @DisplayName("Should handle zero completed courses")
        void shouldHandleZeroCompletedCourses() {
            // Given - This is an edge case, though semantically odd
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 0, 0, 8.0, true
            );

            // When / Then - 0 % 5 == 0, so technically true
            assertThat(event.isMilestone()).isTrue();
        }
    }

    @Nested
    @DisplayName("Record Immutability")
    class RecordImmutability {

        @Test
        @DisplayName("Should be immutable - fields cannot be changed")
        void shouldBeImmutable() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 5, 25, 8.0, true
            );

            // Then - Record fields are final, no setters exist
            // This test verifies the record is properly defined
            assertThat(event.studentId()).isEqualTo(1L);
            assertThat(event.studentName()).isEqualTo("João");
            assertThat(event.completedCourses()).isEqualTo(5);
            assertThat(event.currentCredits()).isEqualTo(25);
            assertThat(event.courseAverage()).isEqualTo(8.0);
            assertThat(event.passed()).isTrue();
        }

        @Test
        @DisplayName("Should implement Serializable")
        void shouldImplementSerializable() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "João", 5, 25, 8.0, true
            );

            // Then
            assertThat(event).isInstanceOf(java.io.Serializable.class);
        }
    }

    @Nested
    @DisplayName("Record Constructor")
    class RecordConstructor {

        @Test
        @DisplayName("Should allow direct construction with all fields")
        void shouldAllowDirectConstructionWithAllFields() {
            // Given
            LocalDateTime occurredAt = LocalDateTime.now();

            // When
            CourseCompletedEvent event = new CourseCompletedEvent(
                    1L,
                    "Direct",
                    3,
                    15,
                    7.5,
                    true,
                    occurredAt,
                    "CUSTOM_TYPE"
            );

            // Then
            assertThat(event.studentId()).isEqualTo(1L);
            assertThat(event.studentName()).isEqualTo("Direct");
            assertThat(event.completedCourses()).isEqualTo(3);
            assertThat(event.currentCredits()).isEqualTo(15);
            assertThat(event.courseAverage()).isEqualTo(7.5);
            assertThat(event.passed()).isTrue();
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
            assertThat(event.eventType()).isEqualTo("CUSTOM_TYPE");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null studentName")
        void shouldHandleNullStudentName() {
            // When
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, null, 1, 5, 7.0, true
            );

            // Then
            assertThat(event.studentName()).isNull();
            assertThat(event.deservesCertificate()).isTrue();
        }

        @Test
        @DisplayName("Should handle negative credits")
        void shouldHandleNegativeCredits() {
            // When - Edge case, should not happen in practice
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", 1, -5, 7.0, true
            );

            // Then
            assertThat(event.currentCredits()).isEqualTo(-5);
        }

        @Test
        @DisplayName("Should handle average exactly at threshold")
        void shouldHandleAverageExactlyAtThreshold() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", 1, 5, 7.0, true
            );

            // When / Then
            assertThat(event.deservesCertificate()).isTrue();
        }

        @Test
        @DisplayName("Should handle average just below threshold")
        void shouldHandleAverageJustBelowThreshold() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                    1L, "Test", 1, 5, 6.999999, true
            );

            // When / Then
            assertThat(event.deservesCertificate()).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality and HashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("Should be equal for same field values")
        void shouldBeEqualForSameFieldValues() {
            // Given
            LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
            
            CourseCompletedEvent event1 = new CourseCompletedEvent(
                    1L, "João", 5, 25, 8.0, true, fixedTime, "COURSE_COMPLETED"
            );
            CourseCompletedEvent event2 = new CourseCompletedEvent(
                    1L, "João", 5, 25, 8.0, true, fixedTime, "COURSE_COMPLETED"
            );

            // Then
            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different field values")
        void shouldNotBeEqualForDifferentFieldValues() {
            // Given
            CourseCompletedEvent event1 = CourseCompletedEvent.of(1L, "João", 5, 25, 8.0, true);
            CourseCompletedEvent event2 = CourseCompletedEvent.of(2L, "Maria", 3, 15, 7.0, true);

            // Then
            assertThat(event1).isNotEqualTo(event2);
        }
    }
}

