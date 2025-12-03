package com.example.subscription.infrastructure.messaging;

import com.example.subscription.domain.event.CourseCompletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para o GamificationEventConsumer.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Guilherme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Processamento de eventos de conclusão de curso</li>
 *   <li>Processamento de notificações</li>
 *   <li>Processamento de analytics</li>
 *   <li>Tratamento de eventos com certificado</li>
 *   <li>Tratamento de eventos de milestone</li>
 * </ul>
 * 
 * <h2>Arquitetura:</h2>
 * <p>Esta classe pertence à camada de Infrastructure, implementando
 * o padrão Consumer para receber mensagens do message broker.</p>
 * 
 * <h2>Nota:</h2>
 * <p>Os métodos do consumer apenas fazem logging, então os testes
 * verificam que não há exceções e que o processamento é executado corretamente.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GamificationEventConsumer Tests - @Guilherme")
class GamificationEventConsumerTest {

    @InjectMocks
    private GamificationEventConsumer consumer;

    // Helper para criar eventos de teste
    private CourseCompletedEvent createEventWithCertificate() {
        return CourseCompletedEvent.of(
            1L,
            "João Silva",
            5,     // completedCourses
            150,   // currentCredits
            8.5,   // courseAverage >= 7.0 para certificado
            true   // passed
        );
    }

    private CourseCompletedEvent createEventWithoutCertificate() {
        return CourseCompletedEvent.of(
            2L,
            "Maria Santos",
            3,     // completedCourses
            75,    // currentCredits
            6.5,   // courseAverage < 7.0, sem certificado mesmo aprovada
            true   // passed
        );
    }

    private CourseCompletedEvent createFailedEvent() {
        return CourseCompletedEvent.of(
            3L,
            "Pedro Alves",
            2,     // completedCourses
            50,    // currentCredits
            4.0,   // courseAverage
            false  // passed
        );
    }

    private CourseCompletedEvent createMilestoneEvent() {
        return CourseCompletedEvent.of(
            4L,
            "Ana Costa",
            10,    // completedCourses - múltiplo de 5, é milestone
            300,   // currentCredits
            9.0,   // courseAverage
            true   // passed
        );
    }

    @Nested
    @DisplayName("Process Course Completed")
    class ProcessCourseCompleted {

        @Test
        @DisplayName("Should process course completed event without exception")
        void shouldProcessCourseCompletedEventWithoutException() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process passed student event")
        void shouldProcessPassedStudentEvent() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then - Não deve lançar exceção
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process failed student event")
        void shouldProcessFailedStudentEvent() {
            // Given
            CourseCompletedEvent event = createFailedEvent();

            // When/Then - Não deve lançar exceção
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process event with certificate")
        void shouldProcessEventWithCertificate() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();
            assertThat(event.deservesCertificate()).isTrue();

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process event without certificate")
        void shouldProcessEventWithoutCertificate() {
            // Given
            CourseCompletedEvent event = createEventWithoutCertificate();
            assertThat(event.deservesCertificate()).isFalse();

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process milestone event")
        void shouldProcessMilestoneEvent() {
            // Given
            CourseCompletedEvent event = createMilestoneEvent();
            assertThat(event.isMilestone()).isTrue();

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process non-milestone event")
        void shouldProcessNonMilestoneEvent() {
            // Given - Criar evento com 3 cursos (não é milestone)
            CourseCompletedEvent nonMilestoneEvent = CourseCompletedEvent.of(
                5L, "Carlos Lima", 3, 100, 8.0, true
            );
            assertThat(nonMilestoneEvent.isMilestone()).isFalse();

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(nonMilestoneEvent))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Process Notification")
    class ProcessNotification {

        @Test
        @DisplayName("Should process notification without exception")
        void shouldProcessNotificationWithoutException() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then
            assertThatCode(() -> consumer.processNotification(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process notification for passed student with certificate")
        void shouldProcessNotificationForPassedStudentWithCertificate() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();
            assertThat(event.passed()).isTrue();
            assertThat(event.deservesCertificate()).isTrue();

            // When/Then
            assertThatCode(() -> consumer.processNotification(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process notification for passed student without certificate")
        void shouldProcessNotificationForPassedStudentWithoutCertificate() {
            // Given
            CourseCompletedEvent event = createEventWithoutCertificate();
            assertThat(event.passed()).isTrue();
            assertThat(event.deservesCertificate()).isFalse();

            // When/Then
            assertThatCode(() -> consumer.processNotification(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process notification for any event")
        void shouldProcessNotificationForAnyEvent() {
            // Given
            CourseCompletedEvent failedEvent = createFailedEvent();

            // When/Then - Mesmo reprovados podem receber notificação (de feedback)
            assertThatCode(() -> consumer.processNotification(failedEvent))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Process Analytics")
    class ProcessAnalytics {

        @Test
        @DisplayName("Should process analytics without exception")
        void shouldProcessAnalyticsWithoutException() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then
            assertThatCode(() -> consumer.processAnalytics(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process analytics for passed event")
        void shouldProcessAnalyticsForPassedEvent() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then
            assertThatCode(() -> consumer.processAnalytics(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process analytics for failed event")
        void shouldProcessAnalyticsForFailedEvent() {
            // Given
            CourseCompletedEvent event = createFailedEvent();

            // When/Then
            assertThatCode(() -> consumer.processAnalytics(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process analytics for milestone event")
        void shouldProcessAnalyticsForMilestoneEvent() {
            // Given
            CourseCompletedEvent event = createMilestoneEvent();

            // When/Then
            assertThatCode(() -> consumer.processAnalytics(event))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Event Processing Integration")
    class EventProcessingIntegration {

        @Test
        @DisplayName("Should process same event through all listeners")
        void shouldProcessSameEventThroughAllListeners() {
            // Given
            CourseCompletedEvent event = createEventWithCertificate();

            // When/Then - Simula que o mesmo evento pode ser processado por todos os listeners
            assertThatCode(() -> {
                consumer.processCourseCompleted(event);
                consumer.processNotification(event);
                consumer.processAnalytics(event);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle multiple events sequentially")
        void shouldHandleMultipleEventsSequentially() {
            // Given
            CourseCompletedEvent event1 = createEventWithCertificate();
            CourseCompletedEvent event2 = createFailedEvent();
            CourseCompletedEvent event3 = createMilestoneEvent();

            // When/Then
            assertThatCode(() -> {
                consumer.processCourseCompleted(event1);
                consumer.processCourseCompleted(event2);
                consumer.processCourseCompleted(event3);
            }).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle event with zero credits")
        void shouldHandleEventWithZeroCredits() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                10L, "Novo Aluno", 1, 0, 7.0, true
            );

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle event with minimum passing grade")
        void shouldHandleEventWithMinimumPassingGrade() {
            // Given - Média exatamente no limite de aprovação
            CourseCompletedEvent event = CourseCompletedEvent.of(
                11L, "Aluno Limite", 1, 10, 6.0, true
            );

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle event with maximum grade")
        void shouldHandleEventWithMaximumGrade() {
            // Given
            CourseCompletedEvent event = CourseCompletedEvent.of(
                12L, "Aluno Exemplar", 10, 500, 10.0, true
            );

            // When/Then
            assertThatCode(() -> consumer.processCourseCompleted(event))
                .doesNotThrowAnyException();
        }
    }
}

