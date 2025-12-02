package com.example.subscription.integration;

import com.example.subscription.domain.entity.Student;
import com.example.subscription.infrastructure.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para funcionalidades de Student.
 * 
 * Este teste demonstra:
 * - Integração completa: Controller → Service → Repository → Database
 * - Testes de queries customizadas do Repository
 * - Validação de endpoints REST
 * - Persistência e consulta de dados
 * 
 * @author Guilherme
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - Student Flow")
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    // ========================================================================
    // TESTES DE API REST
    // ========================================================================

    @Nested
    @DisplayName("API REST Integration Tests")
    class ApiRestTests {

        @Test
        @DisplayName("Should list all students with their data - Full Integration")
        void shouldListAllStudentsWithTheirDataFullIntegration() throws Exception {
            // Given - Cria múltiplos estudantes com diferentes créditos
            studentRepository.save(new Student("Alice", 10));
            studentRepository.save(new Student("Bob", 25));
            studentRepository.save(new Student("Carlos", 5));

            // When/Then - Faz requisição HTTP e valida resposta
            mockMvc.perform(get("/students")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice", "Bob", "Carlos")))
                    .andExpect(jsonPath("$[*].credits", containsInAnyOrder(10, 25, 5)));
        }

        @Test
        @DisplayName("Should get student by ID with correct data - Full Integration")
        void shouldGetStudentByIdWithCorrectDataFullIntegration() throws Exception {
            // Given
            Student student = new Student("Marina", 15);
            student = studentRepository.save(student);
            Long studentId = student.getId();

            // When/Then
            mockMvc.perform(get("/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(studentId))
                    .andExpect(jsonPath("$.name").value("Marina"))
                    .andExpect(jsonPath("$.credits").value(15))
                    .andExpect(jsonPath("$.completedCourses").value(0));
        }

        @Test
        @DisplayName("Should return 404 when student ID does not exist")
        void shouldReturn404WhenStudentIdDoesNotExist() throws Exception {
            // Given - Nenhum estudante criado
            Long nonExistentId = 99999L;

            // When/Then
            mockMvc.perform(get("/students/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value(containsString("Student not found")));
        }

        @Test
        @DisplayName("Should return empty list when no students registered")
        void shouldReturnEmptyListWhenNoStudentsRegistered() throws Exception {
            // Given - Nenhum estudante

            // When/Then
            mockMvc.perform(get("/students")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(content().json("[]"));
        }

        @Test
        @DisplayName("Should handle special characters in student name")
        void shouldHandleSpecialCharactersInStudentName() throws Exception {
            // Given - Nome com caracteres especiais
            Student student = new Student("José María Ñoño", 0);
            student = studentRepository.save(student);

            // When/Then
            mockMvc.perform(get("/students/{id}", student.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("José María Ñoño"));
        }
    }

    // ========================================================================
    // TESTES DE REPOSITORY QUERIES
    // ========================================================================

    @Nested
    @DisplayName("Repository Custom Queries Integration Tests")
    class RepositoryQueryTests {

        @Test
        @DisplayName("Should find students by name")
        void shouldFindStudentsByName() {
            // Given
            Student ana = studentRepository.save(new Student("Ana", 10));
            studentRepository.save(new Student("Bruno", 5));

            // When
            var result = studentRepository.findByName("Ana");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Ana");
            assertThat(result.get().getCredits()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should return empty when name not found")
        void shouldReturnEmptyWhenNameNotFound() {
            // Given
            studentRepository.save(new Student("Ana", 10));

            // When
            var result = studentRepository.findByName("NonExistent");

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should find students with credits above threshold")
        void shouldFindStudentsWithCreditsAboveThreshold() {
            // Given
            studentRepository.save(new Student("Low Credits", 5));
            studentRepository.save(new Student("Medium Credits", 15));
            studentRepository.save(new Student("High Credits", 30));
            studentRepository.save(new Student("Very High Credits", 50));

            // When - Busca estudantes com mais de 10 créditos
            List<Student> result = studentRepository.findByCreditsAmountGreaterThan(10);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(Student::getName)
                    .containsExactlyInAnyOrder("Medium Credits", "High Credits", "Very High Credits");
        }

        @Test
        @DisplayName("Should find students with minimum completed courses")
        void shouldFindStudentsWithMinimumCompletedCourses() {
            // Given
            Student student1 = new Student("Beginner", 0);
            Student student2 = new Student("Intermediate", 5);
            student2.completeCourse(8.0); // +1 curso
            student2.completeCourse(9.0); // +1 curso
            Student student3 = new Student("Advanced", 10);
            student3.completeCourse(8.5);
            student3.completeCourse(7.5);
            student3.completeCourse(9.5);

            studentRepository.save(student1);
            studentRepository.save(student2);
            studentRepository.save(student3);

            // When - Busca estudantes com pelo menos 2 cursos
            List<Student> result = studentRepository.findStudentsWithMinimumCourses(2);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(Student::getName)
                    .containsExactlyInAnyOrder("Intermediate", "Advanced");
        }

        @Test
        @DisplayName("Should count students with minimum credits")
        void shouldCountStudentsWithMinimumCredits() {
            // Given
            studentRepository.save(new Student("Student1", 5));
            studentRepository.save(new Student("Student2", 10));
            studentRepository.save(new Student("Student3", 15));
            studentRepository.save(new Student("Student4", 20));

            // When
            long count = studentRepository.countStudentsWithMinimumCredits(10);

            // Then
            assertThat(count).isEqualTo(3); // Student2, Student3, Student4
        }
    }

    // ========================================================================
    // TESTES DE PERSISTÊNCIA
    // ========================================================================

    @Nested
    @DisplayName("Persistence Integration Tests")
    class PersistenceTests {

        @Test
        @DisplayName("Should persist student and retrieve with same data")
        void shouldPersistStudentAndRetrieveWithSameData() {
            // Given
            Student student = new Student("Persistence Test", 25);
            student.completeCourse(8.5); // Adiciona curso e créditos

            // When
            Student saved = studentRepository.save(student);
            Student retrieved = studentRepository.findById(saved.getId()).orElseThrow();

            // Then
            assertThat(retrieved.getId()).isEqualTo(saved.getId());
            assertThat(retrieved.getName()).isEqualTo("Persistence Test");
            assertThat(retrieved.getCredits()).isEqualTo(28); // 25 + 3
            assertThat(retrieved.getCompletedCourses()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should update student credits via entity methods")
        void shouldUpdateStudentCreditsViaEntityMethods() {
            // Given
            Student student = new Student("Update Test", 10);
            student = studentRepository.save(student);
            Long studentId = student.getId();

            // When - Modifica via métodos de negócio
            Student toUpdate = studentRepository.findById(studentId).orElseThrow();
            toUpdate.addCredits(5);
            toUpdate.deductCredits(3);
            studentRepository.save(toUpdate);

            // Then
            Student updated = studentRepository.findById(studentId).orElseThrow();
            assertThat(updated.getCredits()).isEqualTo(12); // 10 + 5 - 3
        }

        @Test
        @DisplayName("Should delete student correctly")
        void shouldDeleteStudentCorrectly() {
            // Given
            Student student = studentRepository.save(new Student("To Delete", 5));
            Long studentId = student.getId();
            assertThat(studentRepository.findById(studentId)).isPresent();

            // When
            studentRepository.deleteById(studentId);

            // Then
            assertThat(studentRepository.findById(studentId)).isEmpty();
        }

        @Test
        @DisplayName("Should handle multiple students with same initial credits")
        void shouldHandleMultipleStudentsWithSameInitialCredits() {
            // Given
            for (int i = 1; i <= 5; i++) {
                studentRepository.save(new Student("Student " + i, 10));
            }

            // When
            List<Student> allStudents = studentRepository.findAll();
            List<Student> studentsWithMinCredits = studentRepository.findByCreditsAmountGreaterThan(5);

            // Then
            assertThat(allStudents).hasSize(5);
            assertThat(studentsWithMinCredits).hasSize(5);
            assertThat(allStudents).allMatch(s -> s.getCredits() == 10);
        }
    }

    // ========================================================================
    // TESTES DE INTEGRAÇÃO COMPLETA (END-TO-END)
    // ========================================================================

    @Nested
    @DisplayName("End-to-End Integration Tests")
    class EndToEndTests {

        @Test
        @DisplayName("Should create student, complete courses and verify via API")
        void shouldCreateStudentCompleteCoursesAndVerifyViaApi() throws Exception {
            // Given - Cria estudante
            Student student = new Student("E2E Test", 0);
            student = studentRepository.save(student);
            Long studentId = student.getId();

            // When - Simula completar cursos (direto no repository para este teste)
            Student toUpdate = studentRepository.findById(studentId).orElseThrow();
            toUpdate.completeCourse(8.0); // +3 créditos
            toUpdate.completeCourse(9.0); // +3 créditos
            toUpdate.completeCourse(6.5); // +0 créditos (reprovado)
            studentRepository.save(toUpdate);

            // Then - Verifica via API REST
            mockMvc.perform(get("/students/{id}", studentId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completedCourses").value(3))
                    .andExpect(jsonPath("$.credits").value(6)); // 0 + 3 + 3 + 0
        }

        @Test
        @DisplayName("Should reflect repository changes in API response")
        void shouldReflectRepositoryChangesInApiResponse() throws Exception {
            // Given - Estado inicial
            Student student = studentRepository.save(new Student("Reflect Test", 10));
            Long studentId = student.getId();

            // Primeira verificação
            mockMvc.perform(get("/students/{id}", studentId))
                    .andExpect(jsonPath("$.credits").value(10));

            // When - Modifica via repository
            Student toUpdate = studentRepository.findById(studentId).orElseThrow();
            toUpdate.addCredits(20);
            studentRepository.save(toUpdate);

            // Then - Verifica mudança refletida na API
            mockMvc.perform(get("/students/{id}", studentId))
                    .andExpect(jsonPath("$.credits").value(30));
        }

        @Test
        @DisplayName("Should maintain data integrity across operations")
        void shouldMaintainDataIntegrityAcrossOperations() throws Exception {
            // Given
            Student student1 = studentRepository.save(new Student("Student A", 100));
            Student student2 = studentRepository.save(new Student("Student B", 50));

            // When - Operações em ambos os estudantes
            student1 = studentRepository.findById(student1.getId()).orElseThrow();
            student1.deductCredits(30);
            studentRepository.save(student1);

            student2 = studentRepository.findById(student2.getId()).orElseThrow();
            student2.addCredits(30);
            studentRepository.save(student2);

            // Then - Verifica integridade
            mockMvc.perform(get("/students"))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[?(@.name == 'Student A')].credits").value(70))
                    .andExpect(jsonPath("$[?(@.name == 'Student B')].credits").value(80));

            // Soma total de créditos deve ser mantida (150)
            List<Student> all = studentRepository.findAll();
            int totalCredits = all.stream().mapToInt(Student::getCredits).sum();
            assertThat(totalCredits).isEqualTo(150);
        }
    }
}

