package com.example.subscription.infrastructure.messaging;

import com.example.subscription.domain.event.CourseCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o GamificationEventPublisher.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Rickelme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Publicação de eventos de conclusão de curso</li>
 *   <li>Publicação de notificações para alunos aprovados</li>
 *   <li>Publicação de analytics para todos os eventos</li>
 *   <li>Tratamento de erros de publicação</li>
 * </ul>
 * 
 * <h2>Arquitetura:</h2>
 * <p>Esta classe pertence à camada de Infrastructure, implementando
 * o padrão Adapter para comunicação com message broker externo.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GamificationEventPublisher Tests - @Rickelme")
class GamificationEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private GamificationEventPublisher publisher;

    @Captor
    private ArgumentCaptor<String> exchangeCaptor;

    @Captor
    private ArgumentCaptor<String> routingKeyCaptor;

    @Captor
    private ArgumentCaptor<CourseCompletedEvent> eventCaptor;

    private static final String EXCHANGE_NAME = "gamification.events";
    private static final String COURSE_COMPLETED_ROUTING_KEY = "course.completed";

    @BeforeEach
    void setUp() {
        // Configurar valores via reflection para simular @Value
        ReflectionTestUtils.setField(publisher, "exchangeName", EXCHANGE_NAME);
        ReflectionTestUtils.setField(publisher, "courseCompletedRoutingKey", COURSE_COMPLETED_ROUTING_KEY);
    }

    private CourseCompletedEvent createPassedEvent() {
        return CourseCompletedEvent.of(
            1L,
            "João Silva",
            5,     // completedCourses
            150,   // currentCredits
            8.5,   // courseAverage
            true   // passed
        );
    }

    private CourseCompletedEvent createFailedEvent() {
        return CourseCompletedEvent.of(
            2L,
            "Maria Santos",
            3,     // completedCourses
            75,    // currentCredits
            5.0,   // courseAverage
            false  // not passed
        );
    }

    @Nested
    @DisplayName("Publish Course Completed Event")
    class PublishCourseCompletedEvent {

        @Test
        @DisplayName("Should publish event to exchange with correct routing key")
        void shouldPublishEventToExchangeWithCorrectRoutingKey() {
            // Given
            CourseCompletedEvent event = createPassedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then - Verifica publicação principal
            verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq(COURSE_COMPLETED_ROUTING_KEY),
                eq(event)
            );
        }

        @Test
        @DisplayName("Should publish notification when student passed")
        void shouldPublishNotificationWhenStudentPassed() {
            // Given
            CourseCompletedEvent event = createPassedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then - Verifica que notificação foi publicada
            verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq("notification.course.completed"),
                eq(event)
            );
        }

        @Test
        @DisplayName("Should NOT publish notification when student failed")
        void shouldNotPublishNotificationWhenStudentFailed() {
            // Given
            CourseCompletedEvent event = createFailedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then - Verifica que notificação NÃO foi publicada
            verify(rabbitTemplate, never()).convertAndSend(
                eq(EXCHANGE_NAME),
                eq("notification.course.completed"),
                any(CourseCompletedEvent.class)
            );
        }

        @Test
        @DisplayName("Should always publish analytics")
        void shouldAlwaysPublishAnalytics() {
            // Given
            CourseCompletedEvent passedEvent = createPassedEvent();
            CourseCompletedEvent failedEvent = createFailedEvent();

            // When
            publisher.publishCourseCompleted(passedEvent);
            publisher.publishCourseCompleted(failedEvent);

            // Then - Analytics deve ser chamado para ambos os eventos
            verify(rabbitTemplate, times(2)).convertAndSend(
                eq(EXCHANGE_NAME),
                eq("analytics.gamification"),
                any(CourseCompletedEvent.class)
            );
        }

        @Test
        @DisplayName("Should publish all messages in correct order")
        void shouldPublishAllMessagesInCorrectOrder() {
            // Given
            CourseCompletedEvent event = createPassedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then - Verifica ordem: 1. course.completed, 2. notification, 3. analytics
            var inOrder = inOrder(rabbitTemplate);
            inOrder.verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq(COURSE_COMPLETED_ROUTING_KEY),
                eq(event)
            );
            inOrder.verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq("notification.course.completed"),
                eq(event)
            );
            inOrder.verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq("analytics.gamification"),
                eq(event)
            );
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle exception when publishing main event")
        void shouldHandleExceptionWhenPublishingMainEvent() {
            // Given
            CourseCompletedEvent event = createPassedEvent();
            doThrow(new AmqpException("Connection failed"))
                .when(rabbitTemplate)
                .convertAndSend(eq(EXCHANGE_NAME), eq(COURSE_COMPLETED_ROUTING_KEY), any(CourseCompletedEvent.class));

            // When/Then - Não deve lançar exceção
            assertThatCode(() -> publisher.publishCourseCompleted(event))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle exception when publishing notification and continue to analytics")
        void shouldHandleExceptionWhenPublishingNotification() {
            // Given - Evento aprovado (vai chamar publishNotification)
            CourseCompletedEvent event = createPassedEvent();
            
            // Configurar: primeira chamada OK, segunda (notification) falha, terceira (analytics) OK
            doNothing()
                .doThrow(new AmqpException("Notification queue unavailable"))
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(CourseCompletedEvent.class));

            // When/Then - Não deve lançar exceção (catch interno captura)
            assertThatCode(() -> publisher.publishCourseCompleted(event))
                .doesNotThrowAnyException();
            
            // Verifica que analytics ainda foi tentado (3 chamadas no total)
            verify(rabbitTemplate, times(3)).convertAndSend(
                anyString(), anyString(), any(CourseCompletedEvent.class)
            );
        }

        @Test
        @DisplayName("Should handle exception when publishing analytics")
        void shouldHandleExceptionWhenPublishingAnalytics() {
            // Given - Evento reprovado (não vai chamar publishNotification, só analytics)
            CourseCompletedEvent event = createFailedEvent();
            
            // Configurar: primeira chamada OK, segunda (analytics) falha
            doNothing()
                .doThrow(new AmqpException("Analytics queue unavailable"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(CourseCompletedEvent.class));

            // When/Then - Não deve lançar exceção (catch interno captura)
            assertThatCode(() -> publisher.publishCourseCompleted(event))
                .doesNotThrowAnyException();
            
            // Verifica que ambas as chamadas foram feitas (course.completed + analytics)
            verify(rabbitTemplate, times(2)).convertAndSend(
                anyString(), anyString(), any(CourseCompletedEvent.class)
            );
        }

        @Test
        @DisplayName("Should call all three publish methods for passed student")
        void shouldCallAllThreePublishMethodsForPassedStudent() {
            // Given - Um evento de aluno aprovado deve publicar em 3 filas
            CourseCompletedEvent event = createPassedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then - Verifica que foram feitas exatamente 3 chamadas de convertAndSend
            verify(rabbitTemplate, times(3)).convertAndSend(
                anyString(),
                anyString(),
                any(CourseCompletedEvent.class)
            );
        }

        @Test
        @DisplayName("Should handle main exception and not crash")
        void shouldHandleMainExceptionAndNotCrash() {
            // Given - Configurar primeira chamada para falhar
            CourseCompletedEvent event = createPassedEvent();
            doThrow(new AmqpException("RabbitMQ unavailable"))
                .when(rabbitTemplate)
                .convertAndSend(eq(EXCHANGE_NAME), eq(COURSE_COMPLETED_ROUTING_KEY), any(CourseCompletedEvent.class));

            // When/Then - Não deve lançar exceção, apenas logar o erro
            assertThatCode(() -> publisher.publishCourseCompleted(event))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Event Data Integrity")
    class EventDataIntegrity {

        @Test
        @DisplayName("Should preserve event data when publishing")
        void shouldPreserveEventDataWhenPublishing() {
            // Given
            CourseCompletedEvent event = createPassedEvent();

            // When
            publisher.publishCourseCompleted(event);

            // Then
            verify(rabbitTemplate).convertAndSend(
                anyString(),
                eq(COURSE_COMPLETED_ROUTING_KEY),
                eventCaptor.capture()
            );

            CourseCompletedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.studentId()).isEqualTo(event.studentId());
            assertThat(capturedEvent.studentName()).isEqualTo(event.studentName());
            assertThat(capturedEvent.courseAverage()).isEqualTo(event.courseAverage());
            assertThat(capturedEvent.passed()).isEqualTo(event.passed());
        }
    }
}

