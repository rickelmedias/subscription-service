package com.example.subscription.application.service;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.dto.StudentDTO;
import com.example.subscription.domain.constant.BusinessRules;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.domain.event.CourseCompletedEvent;
import com.example.subscription.domain.valueobject.CourseAverage;
import com.example.subscription.infrastructure.messaging.GamificationEventPublisher;
import com.example.subscription.infrastructure.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Application Service para Gamificação de Estudantes.
 * 
 * <h2>Clean Architecture - Application Layer:</h2>
 * <ul>
 *   <li><b>Use Case</b>: Implementa o caso de uso "Completar Curso"</li>
 *   <li><b>Coordenação</b>: Orquestra Repository + Domain Entity</li>
 *   <li><b>Validação</b>: Delega para Value Objects (fail-fast)</li>
 *   <li><b>Transaction</b>: Gerencia transações com @Transactional</li>
 * </ul>
 * 
 * <h2>Fluxo de Execução:</h2>
 * <ol>
 *   <li>Recebe DTO com média do curso</li>
 *   <li>Cria Value Object CourseAverage (valida automaticamente)</li>
 *   <li>Busca Student no Repository</li>
 *   <li>Invoca lógica de domínio: student.completeCourse()</li>
 *   <li>JPA persiste automaticamente (dirty checking)</li>
 *   <li><b>Publica evento para RabbitMQ</b> (Event-Driven Architecture)</li>
 *   <li>Retorna DTO com dados atualizados</li>
 * </ol>
 * 
 * @author Guilherme
 * @see Student#completeCourse(CourseAverage)
 * @see CourseAverage Value Object com validação
 * @see GamificationEventPublisher Publisher de eventos para RabbitMQ
 */
@Service
public class GamificationService {

    private static final Logger log = LoggerFactory.getLogger(GamificationService.class);

    private final StudentRepository studentRepository;
    private final GamificationEventPublisher eventPublisher;

    @Autowired
    public GamificationService(
            StudentRepository studentRepository,
            GamificationEventPublisher eventPublisher) {
        this.studentRepository = studentRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Completa um curso e aplica gamificação.
     * 
     * <p>Após a conclusão, publica evento para RabbitMQ permitindo que
     * outros serviços reajam (gerar certificado, enviar email, analytics).</p>
     * 
     * @param studentId ID do estudante
     * @param request dados da conclusão (média)
     * @return DTO com dados atualizados
     * @throws IllegalArgumentException se média inválida
     * @throws NoSuchElementException se estudante não encontrado
     */
    @Transactional
    public StudentDTO completeCourse(Long studentId, CourseCompletionRequestDTO request) {
        // 1. Validar entrada (cria Value Object que auto-valida)
        CourseAverage average = CourseAverage.of(request.getAverage());
        
        // 2. Buscar estudante
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));
        
        // 3. Aplicar lógica de negócio (no domínio)
        student.completeCourse(average);
        
        // 4. O @Transactional salva automaticamente (dirty checking do JPA)
        
        // 5. Publicar evento para RabbitMQ (Event-Driven Architecture)
        boolean passed = average.isAbove(BusinessRules.PASSING_GRADE_THRESHOLD);
        publishCourseCompletedEvent(student, average.getValue(), passed);
        
        log.info("Curso completado para estudante: {} (Aprovado: {})", 
                student.getName(), passed);
        
        // 6. Retornar DTO
        return StudentDTO.fromEntity(student);
    }

    /**
     * Publica evento de conclusão de curso para RabbitMQ.
     * 
     * @param student Estudante que completou o curso
     * @param courseAverage Média obtida
     * @param passed Se foi aprovado
     */
    private void publishCourseCompletedEvent(Student student, double courseAverage, boolean passed) {
        CourseCompletedEvent event = CourseCompletedEvent.of(
            student.getId(),
            student.getName(),
            student.getCompletedCourses(),
            student.getCredits(),
            courseAverage,
            passed
        );
        
        eventPublisher.publishCourseCompleted(event);
    }
}