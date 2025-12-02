package com.example.subscription.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para arquitetura orientada a eventos.
 * 
 * <h2>Clean Architecture - Infrastructure Layer:</h2>
 * <ul>
 *   <li><b>Adapter</b>: Configura integração com message broker externo</li>
 *   <li><b>Event-Driven</b>: Suporta publicação/consumo de eventos de domínio</li>
 *   <li><b>Desacoplamento</b>: Permite comunicação assíncrona entre serviços</li>
 * </ul>
 * 
 * <h2>Topologia de Filas:</h2>
 * <pre>
 * Exchange (gamification.events) ─┬─▶ Queue (gamification.course.completed) → Certificados
 *                                 ├─▶ Queue (gamification.notification)     → Notificações
 *                                 └─▶ Queue (gamification.analytics)        → Analytics/BI
 * </pre>
 * 
 * <h2>Caso de Uso:</h2>
 * <p>Quando um aluno completa um curso, o evento é publicado no exchange e
 * distribuído para múltiplas filas, cada uma processando uma tarefa específica.</p>
 * 
 * @author Rickelme
 * @see <a href="https://www.rabbitmq.com/tutorials/tutorial-five-java.html">RabbitMQ Topics</a>
 */
@Configuration
public class RabbitMQConfig {

    // ========== EXCHANGE ==========
    
    @Value("${rabbitmq.exchange.name:gamification.events}")
    private String exchangeName;

    // ========== QUEUES ==========
    
    @Value("${rabbitmq.queue.course-completed:gamification.course.completed}")
    private String courseCompletedQueue;
    
    @Value("${rabbitmq.queue.notification:gamification.notification}")
    private String notificationQueue;
    
    @Value("${rabbitmq.queue.analytics:gamification.analytics}")
    private String analyticsQueue;

    // ========== ROUTING KEYS ==========
    
    @Value("${rabbitmq.routing-key.course-completed:course.completed}")
    private String courseCompletedRoutingKey;
    
    @Value("${rabbitmq.routing-key.notification:notification.#}")
    private String notificationRoutingKey;
    
    @Value("${rabbitmq.routing-key.analytics:analytics.#}")
    private String analyticsRoutingKey;

    // ========== EXCHANGE BEAN ==========
    
    /**
     * Exchange do tipo Topic para roteamento flexível de mensagens.
     * Permite usar wildcards (* e #) nas routing keys.
     */
    @Bean
    public TopicExchange gamificationExchange() {
        return new TopicExchange(exchangeName);
    }

    // ========== QUEUE BEANS ==========
    
    /**
     * Fila para processar conclusões de curso (gerar certificados, etc.)
     */
    @Bean
    public Queue courseCompletedQueue() {
        return QueueBuilder.durable(courseCompletedQueue)
                .withArgument("x-message-ttl", 86400000) // 24h TTL
                .build();
    }
    
    /**
     * Fila para notificações (emails, push, etc.)
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueue)
                .withArgument("x-message-ttl", 3600000) // 1h TTL
                .build();
    }
    
    /**
     * Fila para analytics e BI
     */
    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(analyticsQueue)
                .withArgument("x-message-ttl", 604800000) // 7 dias TTL
                .build();
    }

    // ========== BINDINGS ==========
    
    /**
     * Binding: Exchange → Queue de cursos completados
     */
    @Bean
    public Binding courseCompletedBinding(Queue courseCompletedQueue, TopicExchange gamificationExchange) {
        return BindingBuilder
                .bind(courseCompletedQueue)
                .to(gamificationExchange)
                .with(courseCompletedRoutingKey);
    }
    
    /**
     * Binding: Exchange → Queue de notificações
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange gamificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(gamificationExchange)
                .with(notificationRoutingKey);
    }
    
    /**
     * Binding: Exchange → Queue de analytics
     */
    @Bean
    public Binding analyticsBinding(Queue analyticsQueue, TopicExchange gamificationExchange) {
        return BindingBuilder
                .bind(analyticsQueue)
                .to(gamificationExchange)
                .with(analyticsRoutingKey);
    }

    // ========== MESSAGE CONVERTER ==========
    
    /**
     * Conversor JSON para serialização de mensagens.
     * Permite enviar/receber objetos Java como JSON.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ========== RABBIT TEMPLATE ==========
    
    /**
     * Template configurado para envio de mensagens com JSON.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setExchange(exchangeName);
        return template;
    }
}

