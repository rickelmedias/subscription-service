package com.example.subscription.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a configuração do RabbitMQ.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Guilherme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Criação do TopicExchange</li>
 *   <li>Criação das filas (course-completed, notification, analytics)</li>
 *   <li>Bindings entre exchange e filas</li>
 *   <li>MessageConverter JSON</li>
 *   <li>RabbitTemplate configurado</li>
 * </ul>
 * 
 * <h2>Arquitetura:</h2>
 * <p>Esta classe de configuração pertence à camada de Infrastructure,
 * configurando a integração com message broker externo para arquitetura
 * orientada a eventos.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("RabbitMQConfig Tests - @Guilherme")
class RabbitMQConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Nested
    @DisplayName("Configuration Class")
    class ConfigurationClass {

        @Test
        @DisplayName("Should be a Spring Configuration class")
        void shouldBeASpringConfiguration() {
            assertThat(RabbitMQConfig.class)
                .hasAnnotation(Configuration.class);
        }

        @Test
        @DisplayName("Should be autowired correctly")
        void shouldBeAutowiredCorrectly() {
            assertThat(rabbitMQConfig).isNotNull();
        }
    }

    @Nested
    @DisplayName("Exchange Configuration")
    class ExchangeConfiguration {

        @Autowired
        private TopicExchange gamificationExchange;

        @Test
        @DisplayName("Should create TopicExchange bean")
        void shouldCreateTopicExchangeBean() {
            assertThat(gamificationExchange).isNotNull();
        }

        @Test
        @DisplayName("Should have exchange name configured")
        void shouldHaveExchangeNameConfigured() {
            assertThat(gamificationExchange.getName()).isNotBlank();
        }

        @Test
        @DisplayName("Should be a TopicExchange type")
        void shouldBeTopicExchangeType() {
            assertThat(gamificationExchange.getType()).isEqualTo("topic");
        }
    }

    @Nested
    @DisplayName("Queue Configuration")
    class QueueConfiguration {

        @Autowired
        private Queue courseCompletedQueue;

        @Autowired
        private Queue notificationQueue;

        @Autowired
        private Queue analyticsQueue;

        @Test
        @DisplayName("Should create courseCompletedQueue bean")
        void shouldCreateCourseCompletedQueueBean() {
            assertThat(courseCompletedQueue).isNotNull();
            assertThat(courseCompletedQueue.isDurable()).isTrue();
        }

        @Test
        @DisplayName("Should create notificationQueue bean")
        void shouldCreateNotificationQueueBean() {
            assertThat(notificationQueue).isNotNull();
            assertThat(notificationQueue.isDurable()).isTrue();
        }

        @Test
        @DisplayName("Should create analyticsQueue bean")
        void shouldCreateAnalyticsQueueBean() {
            assertThat(analyticsQueue).isNotNull();
            assertThat(analyticsQueue.isDurable()).isTrue();
        }

        @Test
        @DisplayName("Should have TTL configured on queues")
        void shouldHaveTtlConfiguredOnQueues() {
            // Verifica que as filas têm argumentos (TTL configurado)
            assertThat(courseCompletedQueue.getArguments()).containsKey("x-message-ttl");
            assertThat(notificationQueue.getArguments()).containsKey("x-message-ttl");
            assertThat(analyticsQueue.getArguments()).containsKey("x-message-ttl");
        }

        @Test
        @DisplayName("Should have correct TTL values")
        void shouldHaveCorrectTtlValues() {
            // Course completed: 24h (86400000ms)
            assertThat(courseCompletedQueue.getArguments().get("x-message-ttl"))
                .isEqualTo(86400000);
            
            // Notification: 1h (3600000ms)
            assertThat(notificationQueue.getArguments().get("x-message-ttl"))
                .isEqualTo(3600000);
            
            // Analytics: 7 dias (604800000ms)
            assertThat(analyticsQueue.getArguments().get("x-message-ttl"))
                .isEqualTo(604800000);
        }
    }

    @Nested
    @DisplayName("Binding Configuration")
    class BindingConfiguration {

        @Autowired
        private Binding courseCompletedBinding;

        @Autowired
        private Binding notificationBinding;

        @Autowired
        private Binding analyticsBinding;

        @Test
        @DisplayName("Should create courseCompletedBinding bean")
        void shouldCreateCourseCompletedBindingBean() {
            assertThat(courseCompletedBinding).isNotNull();
            assertThat(courseCompletedBinding.getRoutingKey()).isNotBlank();
        }

        @Test
        @DisplayName("Should create notificationBinding bean")
        void shouldCreateNotificationBindingBean() {
            assertThat(notificationBinding).isNotNull();
            assertThat(notificationBinding.getRoutingKey()).isNotBlank();
        }

        @Test
        @DisplayName("Should create analyticsBinding bean")
        void shouldCreateAnalyticsBindingBean() {
            assertThat(analyticsBinding).isNotNull();
            assertThat(analyticsBinding.getRoutingKey()).isNotBlank();
        }

        @Test
        @DisplayName("Should bind to correct exchange")
        void shouldBindToCorrectExchange() {
            assertThat(courseCompletedBinding.getExchange()).isNotBlank();
            assertThat(notificationBinding.getExchange()).isNotBlank();
            assertThat(analyticsBinding.getExchange()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Message Converter Configuration")
    class MessageConverterConfiguration {

        @Autowired
        private MessageConverter jsonMessageConverter;

        @Test
        @DisplayName("Should create MessageConverter bean")
        void shouldCreateMessageConverterBean() {
            assertThat(jsonMessageConverter).isNotNull();
        }

        @Test
        @DisplayName("Should be Jackson2JsonMessageConverter instance")
        void shouldBeJacksonMessageConverterInstance() {
            assertThat(jsonMessageConverter)
                .isInstanceOf(Jackson2JsonMessageConverter.class);
        }
    }

    @Nested
    @DisplayName("RabbitTemplate Configuration")
    class RabbitTemplateConfiguration {

        @Autowired
        private RabbitTemplate rabbitTemplate;

        @Test
        @DisplayName("Should create RabbitTemplate bean")
        void shouldCreateRabbitTemplateBean() {
            assertThat(rabbitTemplate).isNotNull();
        }

        @Test
        @DisplayName("Should have MessageConverter configured")
        void shouldHaveMessageConverterConfigured() {
            assertThat(rabbitTemplate.getMessageConverter())
                .isInstanceOf(Jackson2JsonMessageConverter.class);
        }

        @Test
        @DisplayName("Should have exchange configured")
        void shouldHaveExchangeConfigured() {
            assertThat(rabbitTemplate.getExchange()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Bean Names in Context")
    class BeanNamesInContext {

        @Test
        @DisplayName("Should have all RabbitMQ beans in context")
        void shouldHaveAllRabbitMqBeansInContext() {
            assertThat(applicationContext.containsBean("gamificationExchange")).isTrue();
            assertThat(applicationContext.containsBean("courseCompletedQueue")).isTrue();
            assertThat(applicationContext.containsBean("notificationQueue")).isTrue();
            assertThat(applicationContext.containsBean("analyticsQueue")).isTrue();
            assertThat(applicationContext.containsBean("courseCompletedBinding")).isTrue();
            assertThat(applicationContext.containsBean("notificationBinding")).isTrue();
            assertThat(applicationContext.containsBean("analyticsBinding")).isTrue();
            assertThat(applicationContext.containsBean("jsonMessageConverter")).isTrue();
            assertThat(applicationContext.containsBean("rabbitTemplate")).isTrue();
        }
    }
}

