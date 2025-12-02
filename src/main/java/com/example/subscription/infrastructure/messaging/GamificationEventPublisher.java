package com.example.subscription.infrastructure.messaging;

import com.example.subscription.domain.event.CourseCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publisher de eventos de gamificação para RabbitMQ.
 * 
 * <h2>Clean Architecture - Infrastructure Layer:</h2>
 * <ul>
 *   <li><b>Adapter</b>: Implementa comunicação com message broker externo</li>
 *   <li><b>Port Pattern</b>: Pode ser abstraído via interface para testes</li>
 *   <li><b>Single Responsibility</b>: Apenas publica eventos, não processa</li>
 * </ul>
 * 
 * <h2>Fluxo de Publicação:</h2>
 * <pre>
 * GamificationService.completeCourse()
 *         │
 *         ▼
 * GamificationEventPublisher.publishCourseCompleted()
 *         │
 *         ▼
 * RabbitTemplate.convertAndSend()
 *         │
 *         ▼
 * Exchange: gamification.events
 *         │
 *     ┌───┼───┬────────────┐
 *     ▼   ▼   ▼            ▼
 *   Queue Queue Queue    Queue
 * </pre>
 * 
 * @author Rickelme
 * @see CourseCompletedEvent Evento de domínio publicado
 * @see RabbitMQConfig Configuração de filas e exchange
 */
@Component
public class GamificationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(GamificationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:gamification.events}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.course-completed:course.completed}")
    private String courseCompletedRoutingKey;

    @Autowired
    public GamificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica evento de conclusão de curso.
     * 
     * <p>O evento é enviado para o exchange e roteado para múltiplas filas
     * baseado nas routing keys configuradas.</p>
     * 
     * @param event Evento de conclusão de curso
     */
    public void publishCourseCompleted(CourseCompletedEvent event) {
        log.info("Publicando evento de conclusão de curso para estudante: {} (ID: {})", 
                event.studentName(), event.studentId());
        
        try {
            // Publica no exchange principal com routing key específica
            rabbitTemplate.convertAndSend(
                exchangeName,
                courseCompletedRoutingKey,
                event
            );
            
            log.debug("Evento publicado com sucesso: {}", event);
            
            // Publica também para notificações se for aprovado
            if (event.passed()) {
                publishNotification(event);
            }
            
            // Publica para analytics (todos os eventos)
            publishAnalytics(event);
            
        } catch (Exception e) {
            log.error("Erro ao publicar evento de conclusão de curso: {}", e.getMessage(), e);
            // Em produção, poderia implementar retry ou dead-letter queue
        }
    }

    /**
     * Publica evento para fila de notificações.
     */
    private void publishNotification(CourseCompletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                exchangeName,
                "notification.course.completed",
                event
            );
            log.debug("Notificação publicada para estudante: {}", event.studentName());
        } catch (Exception e) {
            log.warn("Falha ao publicar notificação: {}", e.getMessage());
        }
    }

    /**
     * Publica evento para fila de analytics.
     */
    private void publishAnalytics(CourseCompletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                exchangeName,
                "analytics.gamification",
                event
            );
            log.debug("Analytics publicado para estudante: {}", event.studentName());
        } catch (Exception e) {
            log.warn("Falha ao publicar analytics: {}", e.getMessage());
        }
    }
}

