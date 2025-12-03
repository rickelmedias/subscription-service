package com.example.subscription.application.service;

import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Application Service para Recomendação de Cursos via IA (LLM).
 * 
 * <h2>Clean Architecture - Application Layer:</h2>
 * <ul>
 *   <li><b>Use Case</b>: Implementa o caso de uso "Recomendar Cursos"</li>
 *   <li><b>Coordenação</b>: Orquestra Repository + LLM (Ollama)</li>
 *   <li><b>Integração IA</b>: Usa LangChain4J para comunicação com LLM local</li>
 * </ul>
 * 
 * <h2>Integração com LangChain4J + Ollama:</h2>
 * <p>Este serviço utiliza um modelo de linguagem local (via Ollama) para
 * gerar recomendações personalizadas de cursos baseadas no perfil do aluno.</p>
 * 
 * <h2>Fluxo de Execução:</h2>
 * <ol>
 *   <li>Recebe ID do estudante</li>
 *   <li>Busca dados do estudante no Repository</li>
 *   <li>Monta prompt personalizado com dados do aluno</li>
 *   <li>Envia para LLM local (Ollama com DeepSeek/Llama)</li>
 *   <li>Retorna recomendações geradas pela IA</li>
 * </ol>
 * 
 * @author Rickelme
 * @see ChatLanguageModel Interface LangChain4J para LLMs
 * @see OllamaConfig Configuração do Ollama
 */
@Service
public class CourseRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(CourseRecommendationService.class);

    private final StudentRepository studentRepository;
    private final ChatLanguageModel chatLanguageModel;

    @Autowired
    public CourseRecommendationService(
            StudentRepository studentRepository,
            ChatLanguageModel chatLanguageModel) {
        this.studentRepository = studentRepository;
        this.chatLanguageModel = chatLanguageModel;
    }

    /**
     * Gera recomendações de cursos personalizadas para um estudante.
     * 
     * <p>Utiliza IA local (Ollama) para analisar o perfil do aluno e
     * sugerir próximos cursos relevantes para sua trilha de aprendizado.</p>
     * 
     * @param studentId ID do estudante
     * @return String com recomendações geradas pela IA
     * @throws NoSuchElementException se estudante não encontrado
     */
    public String recommendCoursesForStudent(Long studentId) {
        log.info("Gerando recomendações de cursos para estudante ID: {}", studentId);
        
        // 1. Buscar dados do estudante
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));
        
        // 2. Montar prompt personalizado
        String prompt = buildRecommendationPrompt(student);
        log.debug("Prompt enviado para LLM: {}", prompt);
        
        // 3. Chamar LLM local (Ollama)
        String recommendation = chatLanguageModel.generate(prompt);
        log.info("Recomendação gerada com sucesso para estudante: {}", student.getName());
        
        return recommendation;
    }

    /**
     * Gera recomendações baseadas em um contexto adicional.
     * 
     * @param studentId ID do estudante
     * @param context Contexto adicional (área de interesse, objetivo, etc.)
     * @return String com recomendações personalizadas
     */
    public String recommendCoursesWithContext(Long studentId, String context) {
        log.info("Gerando recomendações com contexto para estudante ID: {}", studentId);
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found: " + studentId));
        
        String prompt = buildContextualPrompt(student, context);
        return chatLanguageModel.generate(prompt);
    }

    /**
     * Constrói o prompt de recomendação baseado no perfil do aluno.
     */
    private String buildRecommendationPrompt(Student student) {
        return String.format("""
            Você é um assistente educacional especializado em recomendar cursos.
            
            ## Perfil do Aluno:
            - Nome: %s
            - Cursos Completados: %d
            - Créditos Acumulados: %d
            
            ## Tarefa:
            Com base no perfil acima, recomende 3 cursos que o aluno deveria fazer 
            em seguida para evoluir em sua trilha de aprendizado.
            
            ## Formato de Resposta:
            Para cada curso, forneça:
            1. Nome do curso
            2. Por que é relevante para este aluno
            3. Dificuldade estimada (Iniciante/Intermediário/Avançado)
            
            Seja conciso e objetivo nas recomendações.
            """,
            student.getName(),
            student.getCompletedCourses(),
            student.getCredits()
        );
    }

    /**
     * Constrói prompt contextualizado com informações adicionais.
     */
    private String buildContextualPrompt(Student student, String context) {
        return String.format("""
            Você é um assistente educacional especializado em recomendar cursos.
            
            ## Perfil do Aluno:
            - Nome: %s
            - Cursos Completados: %d
            - Créditos Acumulados: %d
            
            ## Contexto/Interesse do Aluno:
            %s
            
            ## Tarefa:
            Com base no perfil e contexto acima, recomende 3 cursos específicos 
            que atendam aos interesses do aluno e ajudem em sua evolução.
            
            ## Formato de Resposta:
            Para cada curso, forneça:
            1. Nome do curso
            2. Por que é relevante para este aluno considerando seu interesse
            3. Dificuldade estimada (Iniciante/Intermediário/Avançado)
            
            Seja conciso e objetivo nas recomendações.
            """,
            student.getName(),
            student.getCompletedCourses(),
            student.getCredits(),
            context
        );
    }
}

