package com.example.subscription.infrastructure.messaging;

import com.example.subscription.domain.event.CourseCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de gamificação do RabbitMQ.
 * 
 * <h2>Clean Architecture - Infrastructure Layer:</h2>
 * <ul>
 *   <li><b>Adapter</b>: Recebe mensagens do broker e processa</li>
 *   <li><b>Event-Driven</b>: Reage a eventos publicados de forma assíncrona</li>
 *   <li><b>Desacoplamento</b>: Consumidor independente do produtor</li>
 * </ul>
 * 
 * <h2>Responsabilidades por Fila:</h2>
 * <ul>
 *   <li><b>course.completed</b>: Gerar certificados, atualizar progresso</li>
 *   <li><b>notification</b>: Enviar emails, push notifications</li>
 *   <li><b>analytics</b>: Registrar métricas, alimentar dashboards</li>
 * </ul>
 * 
 * <h2>Arquitetura Publisher/Consumer:</h2>
 * <pre>
 *                    ┌─────────────────────────────┐
 *                    │        RabbitMQ             │
 *                    │   Exchange: gamification    │
 *                    └──────────┬──────────────────┘
 *                               │
 *          ┌────────────────────┼────────────────────┐
 *          ▼                    ▼                    ▼
 *   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
 *   │course.completed│  │notification  │    │analytics     │
 *   └───────┬──────┘    └──────┬───────┘    └──────┬───────┘
 *           ▼                  ▼                   ▼
 *   processCourseCompleted()  processNotification() processAnalytics()
 *           │                  │                   │
 *           ▼                  ▼                   ▼
 *     Gerar Certificado   Enviar Email        Dashboard BI
 * </pre>
 * 
 * @author Rickelme
 * @see CourseCompletedEvent Evento de domínio consumido
 * @see GamificationEventPublisher Publicador de eventos
 */
@Component
public class GamificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(GamificationEventConsumer.class);

    /**
     * Processa eventos de conclusão de curso.
     * 
     * <p>Responsável por:</p>
     * <ul>
     *   <li>Gerar certificados para alunos aprovados</li>
     *   <li>Atualizar registros de progresso</li>
     *   <li>Verificar milestones (badges, achievements)</li>
     * </ul>
     * 
     * @param event Evento de conclusão de curso
     */
    @RabbitListener(queues = "${rabbitmq.queue.course-completed:gamification.course.completed}")
    public void processCourseCompleted(CourseCompletedEvent event) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("📚 EVENTO RECEBIDO: Curso Completado");
        log.info("═══════════════════════════════════════════════════════════");
        log.info("👤 Estudante: {} (ID: {})", event.studentName(), event.studentId());
        log.info("📊 Média: {}", event.courseAverage());
        log.info("✅ Aprovado: {}", event.passed() ? "SIM" : "NÃO");
        log.info("🎓 Cursos completados: {}", event.completedCourses());
        log.info("💰 Créditos atuais: {}", event.currentCredits());
        log.info("⏰ Data/Hora: {}", event.occurredAt());
        
        // Simula processamento de certificado
        if (event.deservesCertificate()) {
            log.info("🏆 Gerando certificado para {}...", event.studentName());
            // TODO: Implementar geração real de certificado (PDF, etc.)
        }
        
        // Verifica milestone
        if (event.isMilestone()) {
            log.info("🎉 MILESTONE! {} completou {} cursos!", 
                    event.studentName(), event.completedCourses());
            // TODO: Implementar sistema de badges/achievements
        }
        
        log.info("═══════════════════════════════════════════════════════════");
    }

    /**
     * Processa eventos para notificações.
     * 
     * <p>Responsável por:</p>
     * <ul>
     *   <li>Enviar emails de parabéns</li>
     *   <li>Push notifications para app mobile</li>
     *   <li>Notificar responsáveis (se aplicável)</li>
     * </ul>
     * 
     * @param event Evento de conclusão de curso
     */
    @RabbitListener(queues = "${rabbitmq.queue.notification:gamification.notification}")
    public void processNotification(CourseCompletedEvent event) {
        log.info("📧 NOTIFICAÇÃO: Enviando congratulações para {}", event.studentName());
        log.info("   → Email: Parabéns pela conclusão do curso!");
        log.info("   → Média obtida: {}", event.courseAverage());
        
        if (event.deservesCertificate()) {
            log.info("   → 📜 Certificado disponível para download");
        }
        
        // TODO: Implementar envio real de email (JavaMailSender, SendGrid, etc.)
        // TODO: Implementar push notification (Firebase, etc.)
    }

    /**
     * Processa eventos para analytics e BI.
     * 
     * <p>Responsável por:</p>
     * <ul>
     *   <li>Registrar métricas de conclusão</li>
     *   <li>Alimentar dashboards de BI</li>
     *   <li>Calcular estatísticas de gamificação</li>
     * </ul>
     * 
     * @param event Evento de conclusão de curso
     */
    @RabbitListener(queues = "${rabbitmq.queue.analytics:gamification.analytics}")
    public void processAnalytics(CourseCompletedEvent event) {
        log.info("📈 ANALYTICS: Registrando métricas");
        log.info("   → Student ID: {}", event.studentId());
        log.info("   → Courses: {}", event.completedCourses());
        log.info("   → Credits: {}", event.currentCredits());
        log.info("   → Average: {}", event.courseAverage());
        log.info("   → Passed: {}", event.passed());
        log.info("   → Timestamp: {}", event.occurredAt());
        
        // TODO: Implementar integração com sistema de BI (Elasticsearch, InfluxDB, etc.)
        // TODO: Calcular métricas agregadas (média geral, taxa de aprovação, etc.)
    }
}

