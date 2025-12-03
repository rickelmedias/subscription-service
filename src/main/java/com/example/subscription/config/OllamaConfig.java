package com.example.subscription.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuração do LangChain4J com Ollama (LLM Local).
 * 
 * <h2>Clean Architecture - Infrastructure Layer:</h2>
 * <ul>
 *   <li><b>Adapter</b>: Configura integração com serviço externo (Ollama)</li>
 *   <li><b>Dependency Injection</b>: Fornece ChatLanguageModel como Bean</li>
 *   <li><b>Configurável</b>: Parâmetros via application.properties</li>
 * </ul>
 * 
 * <h2>Requisitos:</h2>
 * <ul>
 *   <li>Ollama instalado e rodando: <code>ollama serve</code></li>
 *   <li>Modelo baixado: <code>ollama pull deepseek-coder:6.7b</code></li>
 *   <li>GPU AMD com ROCm para aceleração</li>
 * </ul>
 * 
 * <h2>Ambientes:</h2>
 * <ul>
 *   <li><b>Dev/Local</b>: ollama.enabled=true (requer Ollama rodando)</li>
 *   <li><b>Staging/Prod</b>: ollama.enabled=false (usa mock)</li>
 * </ul>
 * 
 * @author Rickelme
 * @see <a href="https://ollama.com">Ollama Documentation</a>
 * @see <a href="https://docs.langchain4j.dev">LangChain4J Documentation</a>
 */
@Configuration
public class OllamaConfig {

    private static final Logger log = LoggerFactory.getLogger(OllamaConfig.class);

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model-name:deepseek-coder:6.7b}")
    private String modelName;

    @Value("${ollama.timeout-seconds:120}")
    private int timeoutSeconds;

    @Value("${ollama.temperature:0.7}")
    private double temperature;

    /**
     * Bean do ChatLanguageModel para injeção nos Services.
     * 
     * <p>Configurado para usar Ollama rodando localmente com GPU AMD.</p>
     * <p>Só é criado quando ollama.enabled=true</p>
     * 
     * @return ChatLanguageModel configurado para Ollama
     */
    @Bean
    @ConditionalOnProperty(name = "ollama.enabled", havingValue = "true", matchIfMissing = false)
    public ChatLanguageModel ollamaChatLanguageModel() {
        log.info("🤖 Inicializando Ollama ChatLanguageModel: {} @ {}", modelName, baseUrl);
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .temperature(temperature)
                .build();
    }

    /**
     * Bean mock do ChatLanguageModel para ambientes sem Ollama.
     * 
     * <p>Usado em staging/prod onde Ollama não está disponível.</p>
     * 
     * @return ChatLanguageModel mock que retorna mensagem padrão
     */
    @Bean
    @ConditionalOnProperty(name = "ollama.enabled", havingValue = "false", matchIfMissing = true)
    public ChatLanguageModel mockChatLanguageModel() {
        log.info("🤖 Usando Mock ChatLanguageModel (Ollama desabilitado)");
        return messages -> Response.from(
            AiMessage.from("⚠️ Serviço de IA não disponível neste ambiente. " +
                "Por favor, tente novamente no ambiente de desenvolvimento com Ollama configurado.")
        );
    }
}

