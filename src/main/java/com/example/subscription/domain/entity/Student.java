package com.example.subscription.domain.entity;

import com.example.subscription.domain.constant.BusinessRules;
import com.example.subscription.domain.valueobject.CourseAverage;
import com.example.subscription.domain.valueobject.Credits;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade Student - Aggregate Root do domínio.
 * 
 * Princípios DDD aplicados:
 * - Entity com identidade única (ID)
 * - Aggregate Root (controla acesso aos Value Objects)
 * - Rich Domain Model (contém lógica de negócio)
 * - Encapsulamento (setters protegidos, lógica via métodos de negócio)
 * 
 * Princípios SOLID aplicados:
 * - Single Responsibility: Gerencia dados e comportamento do estudante
 * - Open/Closed: Extensível via Strategy (cálculo de créditos)
 */
@Entity
@Table(name = "tb_student")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Para JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Para Builder
@Builder
@ToString
@EqualsAndHashCode(of = "id") // Apenas ID para identidade de entidade
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int completedCourses;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "credits", nullable = false))
    private Credits credits;

    // ========== CONSTRUTORES DE NEGÓCIO ==========
    
    /**
     * Construtor para novo estudante (sem créditos)
     */
    public Student(String name) {
        this.name = name;
        this.completedCourses = 0;
        this.credits = Credits.zero();
    }
    
    /**
     * Construtor para estudante com créditos iniciais (usado em testes)
     */
    public Student(String name, int initialCredits) {
        this.name = name;
        this.completedCourses = 0;
        this.credits = Credits.of(initialCredits);
    }

    // ========== MÉTODOS DE NEGÓCIO ==========
    
    /**
     * Completa um curso e aplica gamificação.
     * Este é o método principal da lógica de negócio.
     * 
     * @param average média obtida no curso (Value Object)
     */
    public void completeCourse(CourseAverage average) {
        this.completedCourses++;
        
        if (average.isAbove(BusinessRules.PASSING_GRADE_THRESHOLD)) {
            this.credits = this.credits.add(BusinessRules.CREDITS_PER_APPROVED_COURSE);
        }
    }
    
    /**
     * Sobrecarga para aceitar double (compatibilidade)
     */
    public void completeCourse(double averageValue) {
        completeCourse(CourseAverage.of(averageValue));
    }
    
    /**
     * Adiciona créditos manualmente (ex: bônus, promoções)
     */
    public void addCredits(int amount) {
        this.credits = this.credits.add(amount);
    }
    
    /**
     * Remove créditos (ex: compra de itens, penalidades)
     */
    public void deductCredits(int amount) {
        this.credits = this.credits.subtract(amount);
    }
    
    /**
     * Verifica se tem créditos suficientes
     */
    public boolean hasEnoughCredits(int required) {
        return this.credits.hasAtLeast(required);
    }

    // ========== GETTERS ESPECÍFICOS ==========
    
    /**
     * Getter que retorna o valor int dos créditos (não o Value Object)
     * Necessário para compatibilidade com DTOs e testes
     */
    public int getCredits() {
        return credits != null ? credits.getAmount() : 0;
    }
    
    // ========== SETTERS PROTEGIDOS (Apenas para JPA e Testes) ==========
    
    /**
     * Setter protegido usado apenas pelo JPA.
     * Em código de produção, use os métodos de negócio.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Setter para créditos (usado em testes e migrations).
     * Prefira usar addCredits() ou deductCredits() em código de negócio.
     */
    public void setCredits(int amount) {
        this.credits = Credits.of(amount);
    }
}