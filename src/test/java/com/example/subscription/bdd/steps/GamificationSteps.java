package com.example.subscription.bdd.steps;

import com.example.subscription.application.dto.CourseCompletionRequestDTO;
import com.example.subscription.application.service.GamificationService;
import com.example.subscription.bdd.World;
import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Steps para cenários BDD de Gamificação
 */
public class GamificationSteps {

    @Autowired
    private World world;

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private StudentRepository studentRepository;
    
    @Before
    public void setup() {
        // Reseta mocks antes de cada cenário
        Mockito.reset(studentRepository);
    }

    @Given("a student named {string} with {int} initial credits")
    public void aStudentNamedWithInitialCredits(String name, Integer initialCredits) {
        world.student = new Student(name, initialCredits);
        world.student.setId(1L);
        world.error = null; // Limpa erros anteriores
    }

    @When("the student finishes a course with an average of {double}")
    public void theStudentFinishesACourseWithAnAverageOf(Double average) {
        // Mock do repository para retornar o estudante
        Mockito.when(studentRepository.findById(1L))
               .thenReturn(Optional.of(world.student));
        
        // Executa o serviço
        CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
        request.setAverage(average);
        
        gamificationService.completeCourse(1L, request);
    }

    @When("the student tries to finish a course with the invalid average {double}")
    public void theStudentTriesToFinishACourseWithTheInvalidAverage(Double invalidAverage) {
        // Mock do repository (mas não será chamado se validação falhar antes)
        Mockito.when(studentRepository.findById(1L))
               .thenReturn(Optional.of(world.student));
        
        try {
            CourseCompletionRequestDTO request = new CourseCompletionRequestDTO();
            request.setAverage(invalidAverage);
            
            gamificationService.completeCourse(1L, request);
        } catch (IllegalArgumentException e) {
            world.error = e;
        }
    }

    @Then("the student should have {int} completed course")
    public void theStudentShouldHaveCompletedCourse(Integer expectedCourses) {
        assertThat(world.student.getCompletedCourses())
                .as("Number of completed courses")
                .isEqualTo(expectedCourses);
    }

    @Then("the student's credit balance should be {int}")
    public void theStudentsCreditBalanceShouldBe(Integer expectedCredits) {
        assertThat(world.student.getCredits())
                .as("Credit balance")
                .isEqualTo(expectedCredits);
    }

    @Then("the system should throw an exception with the message {string}")
    public void theSystemShouldThrowAnExceptionWithTheMessage(String expectedMessage) {
        assertThat(world.error)
                .as("Expected exception to be thrown")
                .isNotNull();
        
        assertThat(world.error.getMessage())
                .as("Exception message")
                .isEqualTo(expectedMessage);
    }
}