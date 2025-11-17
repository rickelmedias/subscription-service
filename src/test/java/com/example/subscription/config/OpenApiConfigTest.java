package com.example.subscription.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    @DisplayName("Should create OpenAPI bean")
    void shouldCreateOpenAPIBean() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertThat(openAPI).isNotNull();
    }

    @Test
    @DisplayName("Should have API info configured")
    void shouldHaveAPIInfoConfigured() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Info info = openAPI.getInfo();
        
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isNotBlank();
        assertThat(info.getVersion()).isNotBlank();
        assertThat(info.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("Should have correct API title")
    void shouldHaveCorrectAPITitle() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertThat(openAPI.getInfo().getTitle())
            .contains("Subscription")
            .contains("Gamification");
    }

    @Test
    @DisplayName("Should have version specified")
    void shouldHaveVersionSpecified() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertThat(openAPI.getInfo().getVersion())
            .matches("v\\d+\\.\\d+(\\.\\d+)?"); // vX.X ou vX.X.X
    }

    @Test
    @DisplayName("Should have description")
    void shouldHaveDescription() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertThat(openAPI.getInfo().getDescription())
            .isNotEmpty()
            .containsAnyOf("estudantes", "gamificação", "API", "gerenciamento");
    }

    @Test
    @DisplayName("Should be a Spring Configuration")
    void shouldBeASpringConfiguration() {
        // A correção é verificar a anotação diretamente na classe original,
        // em vez de no proxy CGLIB injetado, que é onde a falha estava ocorrendo.
        assertThat(OpenApiConfig.class)
            .hasAnnotation(Configuration.class);
    }
}