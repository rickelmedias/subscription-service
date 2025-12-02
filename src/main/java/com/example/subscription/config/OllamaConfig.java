package com.example.subscription.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
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
 * @author Rickelme
 * @see <a href="https://ollama.com">Ollama Documentation</a>
 * @see <a href="https://docs.langchain4j.dev">LangChain4J Documentation</a>
 */
@Configuration
public class OllamaConfig {

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
     * 
     * @return ChatLanguageModel configurado para Ollama
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .temperature(temperature)
                .build();
    }
}

