package com.example.subscription.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento de Domínio: Curso Completado.
 * 
 * <h2>DDD - Domain Events:</h2>
 * <ul>
 *   <li><b>Imutabilidade</b>: Evento representa algo que aconteceu (passado)</li>
 *   <li><b>Ubiquitous Language</b>: Nome expressa o que ocorreu no domínio</li>
 *   <li><b>Desacoplamento</b>: Permite reação assíncrona de outros contextos</li>
 * </ul>
 * 
 * <h2>Uso na Arquitetura:</h2>
 * <pre>
 * GamificationService → EventPublisher → RabbitMQ → Consumers
 *         │                                              │
 *         └── Publica evento quando ────────────────────▶│
 *             aluno completa curso                       │
 *                                                        ▼
 *                                              - Gerar certificado
 *                                              - Enviar notificação
 *                                              - Atualizar analytics
 * </pre>
 * 
 * @author Rickelme
 * @see com.example.subscription.infrastructure.messaging.GamificationEventPublisher
 */
public record CourseCompletedEvent(
    Long studentId,
    String studentName,
    int completedCourses,
    int currentCredits,
    double courseAverage,
    boolean passed,
    LocalDateTime occurredAt,
    String eventType
) implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Factory method para criar evento de conclusão de curso.
     * 
     * @param studentId ID do estudante
     * @param studentName Nome do estudante
     * @param completedCourses Total de cursos completados
     * @param currentCredits Créditos atuais
     * @param courseAverage Média obtida no curso
     * @param passed Se o aluno foi aprovado
     * @return Evento imutável
     */
    public static CourseCompletedEvent of(
            Long studentId,
            String studentName,
            int completedCourses,
            int currentCredits,
            double courseAverage,
            boolean passed) {
        
        return new CourseCompletedEvent(
            studentId,
            studentName,
            completedCourses,
            currentCredits,
            courseAverage,
            passed,
            LocalDateTime.now(),
            "COURSE_COMPLETED"
        );
    }
    
    /**
     * Verifica se o aluno merece certificado (aprovado com boa média).
     */
    public boolean deservesCertificate() {
        return passed && courseAverage >= 7.0;
    }
    
    /**
     * Verifica se é um marco significativo (múltiplo de 5 cursos).
     */
    public boolean isMilestone() {
        return completedCourses % 5 == 0;
    }
}

