package com.example.subscription.infrastructure.repository;

import com.example.subscription.domain.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Teste UNITÁRIO de Repository usando @DataJpaTest.
 * Testa TODAS as operações do StudentRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Student Repository Complete Tests")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Limpa dados antes de cada teste
        repository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    // ========== CRUD BÁSICO ==========

    @Test
    @DisplayName("Should save and retrieve student by ID")
    void shouldSaveAndRetrieveStudentById() {
        // Given
        Student student = new Student("João Silva");
        
        // When
        Student saved = repository.save(student);
        entityManager.flush();
        entityManager.clear(); // Limpa cache para forçar busca no DB
        
        Optional<Student> found = repository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("João Silva");
        assertThat(found.get().getCompletedCourses()).isZero();
        assertThat(found.get().getCredits()).isZero();
    }

    @Test
    @DisplayName("Should persist student with initial credits")
    void shouldPersistStudentWithInitialCredits() {
        // Given
        Student student = new Student("Maria", 5);
        
        // When
        Student saved = repository.save(student);
        entityManager.flush();
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCredits()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should update student credits")
    void shouldUpdateStudentCredits() {
        // Given
        Student student = new Student("Carlos", 2);
        Student saved = entityManager.persistFlushFind(student);
        
        // When
        saved.completeCourse(8.5); // Deve ganhar 3 créditos
        repository.save(saved);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Student updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getCredits()).isEqualTo(5); // 2 + 3
        assertThat(updated.getCompletedCourses()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find all students")
    void shouldFindAllStudents() {
        // Given
        entityManager.persist(new Student("Alice"));
        entityManager.persist(new Student("Bob"));
        entityManager.persist(new Student("Charlie"));
        entityManager.flush();
        
        // When
        List<Student> students = repository.findAll();
        
        // Then
        assertThat(students).hasSize(3);
        assertThat(students).extracting(Student::getName)
            .containsExactlyInAnyOrder("Alice", "Bob", "Charlie");
    }

    @Test
    @DisplayName("Should delete student by ID")
    void shouldDeleteStudentById() {
        // Given
        Student student = entityManager.persistFlushFind(new Student("Delete Me"));
        Long id = student.getId();
        
        // When
        repository.deleteById(id);
        entityManager.flush();
        
        // Then
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should delete student by entity")
    void shouldDeleteStudentByEntity() {
        // Given
        Student student = entityManager.persistFlushFind(new Student("Delete Me"));
        
        // When
        repository.delete(student);
        entityManager.flush();
        
        // Then
        assertThat(repository.findById(student.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when student not found")
    void shouldReturnEmptyWhenStudentNotFound() {
        // When
        Optional<Student> found = repository.findById(999L);
        
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if student exists by ID")
    void shouldCheckIfStudentExistsById() {
        // Given
        Student student = entityManager.persistFlushFind(new Student("Test"));
        
        // When/Then
        assertThat(repository.existsById(student.getId())).isTrue();
        assertThat(repository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Should count all students")
    void shouldCountAllStudents() {
        // Given
        entityManager.persist(new Student("Alice"));
        entityManager.persist(new Student("Bob"));
        entityManager.persist(new Student("Charlie"));
        entityManager.flush();
        
        // When
        long count = repository.count();
        
        // Then
        assertThat(count).isEqualTo(3);
    }

    // ========== QUERIES CUSTOMIZADAS ==========

    @Test
    @DisplayName("Should find student by name")
    void shouldFindStudentByName() {
        // Given
        Student student = new Student("Diana Prince");
        entityManager.persistAndFlush(student);
        
        // When
        Optional<Student> found = repository.findByName("Diana Prince");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Diana Prince");
    }

    @Test
    @DisplayName("Should return empty when name not found")
    void shouldReturnEmptyWhenNameNotFound() {
        // When
        Optional<Student> found = repository.findByName("Non Existent");
        
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find students with credits greater than value")
    void shouldFindStudentsWithCreditsGreaterThanValue() {
        // Given
        Student student1 = new Student("Alice", 5);
        Student student2 = new Student("Bob", 10);
        Student student3 = new Student("Charlie", 15);
        Student student4 = new Student("Diana", 2);
        
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);
        entityManager.persist(student4);
        entityManager.flush();
        
        // When
        List<Student> students = repository.findByCreditsAmountGreaterThan(7);
        
        // Then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getName)
            .containsExactlyInAnyOrder("Bob", "Charlie");
    }

    @Test
    @DisplayName("Should return empty list when no students have enough credits")
    void shouldReturnEmptyListWhenNoStudentsHaveEnoughCredits() {
        // Given
        entityManager.persist(new Student("Alice", 5));
        entityManager.persist(new Student("Bob", 3));
        entityManager.flush();
        
        // When
        List<Student> students = repository.findByCreditsAmountGreaterThan(100);
        
        // Then
        assertThat(students).isEmpty();
    }

    @Test
    @DisplayName("Should find students with minimum completed courses")
    void shouldFindStudentsWithMinimumCompletedCourses() {
        // Given
        Student student1 = new Student("Alice", 0);
        student1.completeCourse(8.0);
        student1.completeCourse(9.0);
        
        Student student2 = new Student("Bob", 0);
        student2.completeCourse(8.5);
        student2.completeCourse(9.5);
        student2.completeCourse(7.5);
        
        Student student3 = new Student("Charlie", 0);
        student3.completeCourse(8.0);
        
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);
        entityManager.flush();
        
        // When
        List<Student> students = repository.findStudentsWithMinimumCourses(2);
        
        // Then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getName)
            .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    @DisplayName("Should return empty list when no students have minimum courses")
    void shouldReturnEmptyListWhenNoStudentsHaveMinimumCourses() {
        // Given
        Student student = new Student("Alice", 0);
        student.completeCourse(8.0);
        entityManager.persistAndFlush(student);
        
        // When
        List<Student> students = repository.findStudentsWithMinimumCourses(10);
        
        // Then
        assertThat(students).isEmpty();
    }

    @Test
    @DisplayName("Should count students with minimum credits")
    void shouldCountStudentsWithMinimumCredits() {
        // Given
        entityManager.persist(new Student("Alice", 5));
        entityManager.persist(new Student("Bob", 10));
        entityManager.persist(new Student("Charlie", 15));
        entityManager.persist(new Student("Diana", 2));
        entityManager.flush();
        
        // When
        long count = repository.countStudentsWithMinimumCredits(8);
        
        // Then
        assertThat(count).isEqualTo(2); // Bob e Charlie
    }

    @Test
    @DisplayName("Should return zero when no students have minimum credits")
    void shouldReturnZeroWhenNoStudentsHaveMinimumCredits() {
        // Given
        entityManager.persist(new Student("Alice", 5));
        entityManager.persist(new Student("Bob", 3));
        entityManager.flush();
        
        // When
        long count = repository.countStudentsWithMinimumCredits(100);
        
        // Then
        assertThat(count).isZero();
    }

    // ========== TESTES DE TRANSAÇÃO E PERSISTÊNCIA ==========

    @Test
    @DisplayName("Should cascade save embedded value objects")
    void shouldCascadeSaveEmbeddedValueObjects() {
        // Given
        Student student = new Student("Eduardo", 10);
        
        // When
        Student saved = repository.saveAndFlush(student);
        entityManager.clear();
        
        // Then
        Student found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getCredits()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should handle multiple saves (idempotence)")
    void shouldHandleMultipleSaves() {
        // Given
        Student student = new Student("Fernanda", 5);
        
        // When
        Student saved1 = repository.save(student);
        saved1.addCredits(3);
        Student saved2 = repository.save(saved1);
        entityManager.flush();
        
        // Then
        assertThat(saved2.getCredits()).isEqualTo(8);
        assertThat(repository.count()).isEqualTo(1); // Apenas 1 estudante
    }

    @Test
    @DisplayName("Should delete all students")
    void shouldDeleteAllStudents() {
        // Given
        entityManager.persist(new Student("Alice"));
        entityManager.persist(new Student("Bob"));
        entityManager.persist(new Student("Charlie"));
        entityManager.flush();
        
        // When
        repository.deleteAll();
        entityManager.flush();
        
        // Then
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("Should save and flush immediately")
    void shouldSaveAndFlushImmediately() {
        // Given
        Student student = new Student("Gabriel");
        
        // When
        Student saved = repository.saveAndFlush(student);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        // Verifica que foi persistido imediatamente
        Student found = entityManager.find(Student.class, saved.getId());
        assertThat(found).isNotNull();
    }

    @Test
    @DisplayName("Should handle batch operations efficiently")
    void shouldHandleBatchOperationsEfficiently() {
        // Given
        List<Student> students = List.of(
            new Student("Student1", 5),
            new Student("Student2", 10),
            new Student("Student3", 15)
        );
        
        // When
        List<Student> saved = repository.saveAll(students);
        entityManager.flush();
        
        // Then
        assertThat(saved).hasSize(3);
        assertThat(repository.count()).isEqualTo(3);
    }
}