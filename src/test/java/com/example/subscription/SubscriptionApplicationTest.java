package com.example.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Subscription Application Tests")
class SubscriptionApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load application context successfully")
    void shouldLoadApplicationContextSuccessfully() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Should have all required beans configured")
    void shouldHaveAllRequiredBeansConfigured() {
        // Controllers
        assertThat(applicationContext.containsBean("gamificationController")).isTrue();
        assertThat(applicationContext.containsBean("studentController")).isTrue();
        
        // Services
        assertThat(applicationContext.containsBean("gamificationService")).isTrue();
        assertThat(applicationContext.containsBean("studentService")).isTrue();
        
        // Repository
        assertThat(applicationContext.containsBean("studentRepository")).isTrue();
        
        // Config
        assertThat(applicationContext.containsBean("openApiConfig")).isTrue();
        
        // Strategies
        assertThat(applicationContext.containsBean("standardCreditStrategy")).isTrue();
        assertThat(applicationContext.containsBean("premiumCreditStrategy")).isTrue();
        assertThat(applicationContext.containsBean("creditStrategyFactory")).isTrue();
    }

    @Test
    @DisplayName("Should have Spring Boot running")
    void shouldHaveSpringBootRunning() {
        // Verifica que o contexto está carregado e tem a classe principal
        assertThat(applicationContext.getBean(com.example.subscription.SubscriptionApplication.class)).isNotNull();
    }

    @Test
    @DisplayName("Should load test profile")
    void shouldLoadTestProfile() {
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }

    @Test
    @DisplayName("Should have JPA configured")
    void shouldHaveJPAConfigured() {
        assertThat(applicationContext.containsBean("entityManagerFactory")).isTrue();
        assertThat(applicationContext.containsBean("transactionManager")).isTrue();
    }

    @Test
    @DisplayName("Should have data source configured")
    void shouldHaveDataSourceConfigured() {
        assertThat(applicationContext.containsBean("dataSource")).isTrue();
    }
}