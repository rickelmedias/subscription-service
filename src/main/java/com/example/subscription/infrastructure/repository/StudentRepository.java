package com.example.subscription.infrastructure.repository;

import com.example.subscription.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para acesso aos dados de Student.
 * Utiliza Spring Data JPA para abstrair operações de banco.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Busca estudante por nome (query method do Spring Data)
     */
    Optional<Student> findByName(String name);
    
    /**
     * Busca estudantes com créditos acima de um valor
     * Usa JPQL para acessar o campo embeddado credits.amount
     */
    @Query("SELECT s FROM Student s WHERE s.credits.amount > :minCredits")
    List<Student> findByCreditsAmountGreaterThan(@Param("minCredits") int minCredits);
    
    /**
     * Busca estudantes que completaram pelo menos N cursos
     */
    @Query("SELECT s FROM Student s WHERE s.completedCourses >= :minCourses")
    List<Student> findStudentsWithMinimumCourses(@Param("minCourses") int minCourses);
    
    /**
     * Conta estudantes com pelo menos N créditos
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.credits.amount >= :minCredits")
    long countStudentsWithMinimumCredits(@Param("minCredits") int minCredits);
}