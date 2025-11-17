package com.example.subscription.bdd.steps;

import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Steps para cenários BDD de API de Estudantes
 */
public class StudentApiSteps {

    @Autowired
    private StudentRepository studentRepository;

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;

    @Given("the database has a student named {string}")
    public void theDatabaseHasAStudentNamed(String name) {
        Student student = new Student(name);
        student.setId(1L);
        
        // Mock do repository
        when(studentRepository.findAll()).thenReturn(List.of(student));
    }

    @When("the user sends a GET request to {string}")
    public void theUserSendsAGetRequestTo(String endpoint) {
        String url = "http://localhost:" + port + endpoint;
        response = restTemplate.getForEntity(url, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(Integer statusCode) {
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.valueOf(statusCode));
    }

    @Then("the response body should contain a list with {string}")
    public void theResponseBodyShouldContainAListWith(String name) {
        assertThat(response.getBody())
                .as("Response body")
                .isNotNull()
                .contains(name);
    }
}