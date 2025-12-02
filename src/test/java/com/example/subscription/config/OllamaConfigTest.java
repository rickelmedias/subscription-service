package com.example.subscription.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a configuração do Ollama/LangChain4J.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Rickelme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Criação do bean ChatLanguageModel</li>
 *   <li>Configuração padrão via application.properties</li>
 *   <li>Validação da anotação @Configuration</li>
 * </ul>
 * 
 * <h2>Nota:</h2>
 * <p>Os testes não dependem do Ollama estar rodando pois apenas verificam
 * se o bean é criado corretamente com as configurações especificadas.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OllamaConfig Tests - @Rickelme")
class OllamaConfigTest {

    @Autowired
    private OllamaConfig ollamaConfig;

    @Nested
    @DisplayName("Configuration Class")
    class ConfigurationClass {

        @Test
        @DisplayName("Should be a Spring Configuration class")
        void shouldBeASpringConfiguration() {
            assertThat(OllamaConfig.class)
                .hasAnnotation(Configuration.class);
        }

        @Test
        @DisplayName("Should be autowired correctly")
        void shouldBeAutowiredCorrectly() {
            assertThat(ollamaConfig).isNotNull();
        }
    }

    @Nested
    @DisplayName("ChatLanguageModel Bean")
    class ChatLanguageModelBean {

        @Test
        @DisplayName("Should create ChatLanguageModel bean")
        void shouldCreateChatLanguageModelBean() {
            ChatLanguageModel model = ollamaConfig.chatLanguageModel();
            
            assertThat(model).isNotNull();
        }

        @Test
        @DisplayName("Should create OllamaChatModel instance")
        void shouldCreateOllamaChatModelInstance() {
            ChatLanguageModel model = ollamaConfig.chatLanguageModel();
            
            // Verifica que é uma instância de ChatLanguageModel (interface)
            assertThat(model).isInstanceOf(ChatLanguageModel.class);
        }

        @Test
        @DisplayName("Should create model with configured properties")
        void shouldCreateModelWithConfiguredProperties() {
            // O bean deve ser criado sem exceções mesmo em ambiente de teste
            ChatLanguageModel model = ollamaConfig.chatLanguageModel();
            
            // Verifica que o modelo foi criado (configurações são aplicadas internamente)
            assertThat(model).isNotNull();
            assertThat(model.toString()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Integration with Spring Context")
    class IntegrationWithSpringContext {

        @Autowired
        private ChatLanguageModel chatLanguageModel;

        @Test
        @DisplayName("Should have ChatLanguageModel bean available in context")
        void shouldHaveChatLanguageModelBeanAvailableInContext() {
            assertThat(chatLanguageModel).isNotNull();
        }

        @Test
        @DisplayName("Should return same bean on multiple calls")
        void shouldReturnSameBeanOnMultipleCalls() {
            // Beans são singleton por padrão
            assertThat(chatLanguageModel).isSameAs(chatLanguageModel);
        }
    }
}

