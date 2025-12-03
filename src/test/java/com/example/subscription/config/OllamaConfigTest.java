package com.example.subscription.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a configuração do Ollama/LangChain4J.
 * 
 * <h2>Responsável:</h2>
 * <p>@author Rickelme</p>
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Criação do bean ChatLanguageModel (mock em test profile)</li>
 *   <li>Criação do bean OllamaChatModel (quando habilitado)</li>
 *   <li>Configuração condicional via application.properties</li>
 *   <li>Validação da anotação @Configuration</li>
 * </ul>
 * 
 * <h2>Nota:</h2>
 * <p>Em ambiente de teste (ollama.enabled=false), o mock é usado
 * em vez do Ollama real.</p>
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
    @DisplayName("Mock ChatLanguageModel Bean (Test Profile)")
    class MockChatLanguageModelBean {

        @Test
        @DisplayName("Should create mock ChatLanguageModel bean when ollama.enabled=false")
        void shouldCreateMockChatLanguageModelBean() {
            // Em test profile, ollama.enabled=false, então usa mock
            ChatLanguageModel model = ollamaConfig.mockChatLanguageModel();
            
            assertThat(model).isNotNull();
        }

        @Test
        @DisplayName("Mock should return warning message")
        void mockShouldReturnWarningMessage() {
            ChatLanguageModel model = ollamaConfig.mockChatLanguageModel();
            
            // O mock retorna uma mensagem de aviso
            String response = model.generate("test");
            assertThat(response)
                .contains("Serviço de IA não disponível");
        }
    }

    @Nested
    @DisplayName("Ollama ChatLanguageModel Bean (when enabled)")
    class OllamaChatLanguageModelBean {

        @Test
        @DisplayName("Should create OllamaChatModel when ollamaChatLanguageModel is called")
        void shouldCreateOllamaChatModelWhenCalled() {
            // Criar uma nova instância para testar o método diretamente
            OllamaConfig config = new OllamaConfig();
            
            // Configurar os campos via reflection
            ReflectionTestUtils.setField(config, "baseUrl", "http://localhost:11434");
            ReflectionTestUtils.setField(config, "modelName", "test-model");
            ReflectionTestUtils.setField(config, "timeoutSeconds", 30);
            ReflectionTestUtils.setField(config, "temperature", 0.5);
            
            // When
            ChatLanguageModel model = config.ollamaChatLanguageModel();
            
            // Then
            assertThat(model).isNotNull();
            assertThat(model).isInstanceOf(OllamaChatModel.class);
        }

        @Test
        @DisplayName("Should configure model with provided values")
        void shouldConfigureModelWithProvidedValues() {
            // Criar uma nova instância para testar o método diretamente
            OllamaConfig config = new OllamaConfig();
            
            // Configurar os campos via reflection
            ReflectionTestUtils.setField(config, "baseUrl", "http://custom-url:11434");
            ReflectionTestUtils.setField(config, "modelName", "llama3:8b");
            ReflectionTestUtils.setField(config, "timeoutSeconds", 60);
            ReflectionTestUtils.setField(config, "temperature", 0.8);
            
            // When
            ChatLanguageModel model = config.ollamaChatLanguageModel();
            
            // Then
            assertThat(model).isNotNull();
            // O modelo é criado sem erros com as configurações fornecidas
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
        @DisplayName("Should be mock instance in test profile")
        void shouldBeMockInstanceInTestProfile() {
            // Verifica que é uma instância de ChatLanguageModel
            assertThat(chatLanguageModel).isInstanceOf(ChatLanguageModel.class);
            
            // E que retorna a mensagem de mock
            String response = chatLanguageModel.generate("test");
            assertThat(response)
                .contains("Serviço de IA não disponível");
        }
    }
}
