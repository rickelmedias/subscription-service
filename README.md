# Projeto AC2_CA - Subscription & Gamification API

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura e Padrões](#arquitetura-e-padrões)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Configuração do Projeto](#configuração-do-projeto)
5. [Estrutura do Projeto](#estrutura-do-projeto)
6. [Executando a Aplicação](#executando-a-aplicação)
7. [Testes](#testes)
8. [Docker](#docker)
9. [Jenkins Pipeline](#jenkins-pipeline)
10. [Documentação da API](#documentação-da-api)

---

## 🎯 Visão Geral

Este projeto implementa uma API REST para gerenciamento de estudantes e gamificação de cursos, utilizando Spring Boot 3.3.4, Java 17, e seguindo princípios de **Domain-Driven Design (DDD)**, **SOLID** e padrões de arquitetura limpa.

### Funcionalidades Principais

- ✅ CRUD de estudantes
- ✅ Sistema de gamificação (créditos por cursos completados)
- ✅ Value Objects para encapsulamento de lógica de negócio
- ✅ Strategy Pattern para cálculos de créditos
- ✅ Testes unitários e de integração
- ✅ Pipeline CI/CD com Jenkins
- ✅ Dockerização da aplicação
- ✅ Documentação Swagger/OpenAPI

---

## 🏗️ Arquitetura e Padrões

### 1. Camada Entity (Entidades + Value Objects)

#### Entity: `Student`
- **Localização**: `com.example.subscription.domain.entity.Student`
- **Características**:
  - Aggregate Root do domínio
  - Usa Lombok para reduzir boilerplate (`@Getter`, `@Builder`, `@NoArgsConstructor`)
  - Encapsula lógica de negócio (métodos `completeCourse()`, `addCredits()`, `deductCredits()`)
  - Utiliza Value Objects (`Credits`, `CourseAverage`) para garantir invariantes

#### Value Objects

**Credits** (`com.example.subscription.domain.valueobject.Credits`):
- Imutável (novos valores geram novos objetos)
- Auto-validável (não permite valores negativos)
- Embeddable no JPA (`@Embeddable`)

**CourseAverage** (`com.example.subscription.domain.valueobject.CourseAverage`):
- Valida intervalo de 0.0 a 10.0
- Arredonda para 2 casas decimais
- Métodos de comparação e classificação de performance

### 2. Camada Repository e Padrão JPA

#### Repository: `StudentRepository`
- **Localização**: `com.example.subscription.infrastructure.repository.StudentRepository`
- **Características**:
  - Estende `JpaRepository<Student, Long>`
  - Query methods customizados usando JPQL
  - Acesso correto a campos embeddados (`credits.amount`)

**Métodos disponíveis**:
```java
Optional<Student> findByName(String name);
List<Student> findByCreditsAmountGreaterThan(int minCredits);
List<Student> findStudentsWithMinimumCourses(int minCourses);
long countStudentsWithMinimumCredits(int minCredits);
```

### 3. Configurações de Profiles

#### Arquivos de Properties

**`application.properties`** (Desenvolvimento - H2):
- Banco de dados: H2 em memória
- H2 Console habilitado: `http://localhost:8080/h2-console`
- JPA: `ddl-auto=update`
- Logging: DEBUG para desenvolvimento

**`application-prod.properties`** (Produção - PostgreSQL):
- Banco de dados: PostgreSQL
- JPA: `ddl-auto=update`
- H2 Console: desabilitado
- Logging: INFO

**`application-staging.properties`** (Staging - PostgreSQL):
- Banco de dados: PostgreSQL
- JPA: `ddl-auto=update`
- Logging: DEBUG

**`application-test.properties`** (Testes):
- Banco de dados: H2 em memória
- JPA: `ddl-auto=create-drop`
- H2 Console: desabilitado
- Logging: WARN

### 4. Gerar Schema a partir do ORM

O schema do banco é gerado automaticamente pelo Hibernate/JPA:

**Desenvolvimento (H2)**:
- Acesse: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:subscriptiondb`
- Username: `sa`
- Password: (vazio)

**Produção/Staging (PostgreSQL)**:
- O schema é criado automaticamente via `ddl-auto=update`
- Tabela principal: `tb_student`

### 5. Camada de DTO

#### DTOs Disponíveis

**StudentDTO** (`com.example.subscription.application.dto.StudentDTO`):
- Mapeia Entity para DTO (método `fromEntity()`)
- Usado na camada de apresentação
- Anotações Swagger para documentação

**CourseCompletionRequestDTO** (`com.example.subscription.application.dto.CourseCompletionRequestDTO`):
- Validação de entrada (`@DecimalMin`, `@DecimalMax`)
- Usado no endpoint de conclusão de curso

### 6. Camada Service

#### Services Disponíveis

**StudentService** (`com.example.subscription.application.service.StudentService`):
- CRUD de estudantes
- Métodos: `getAllStudents()`, `getStudentById()`
- Transacional (`@Transactional`)

**GamificationService** (`com.example.subscription.application.service.GamificationService`):
- Aplica regras de gamificação
- Método: `completeCourse()`
- Coordena entre Repository e Domain

### 7. Camada Controller

#### Controllers Disponíveis

**StudentController** (`com.example.subscription.presentation.controller.StudentController`):
- Endpoints:
  - `GET /students` - Lista todos os estudantes
  - `GET /students/{id}` - Busca estudante por ID

**GamificationController** (`com.example.subscription.presentation.controller.GamificationController`):
- Endpoints:
  - `POST /gamification/students/{id}/complete-course` - Completa curso e aplica gamificação

**GlobalExceptionHandler** (`com.example.subscription.presentation.controller.GlobalExceptionHandler`):
- Tratamento global de exceções
- Respostas HTTP consistentes

### 8. Configuração Swagger (OpenAPI)

#### Classe: `OpenApiConfig`
- **Localização**: `com.example.subscription.config.OpenApiConfig`
- **Endpoints**:
  - Swagger UI: `http://localhost:8080/swagger-ui.html`
  - API Docs: `http://localhost:8080/api-docs`

#### Gerar PDF dos Endpoints

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Clique em "Download" → "OpenAPI JSON"
3. Use ferramentas como [Swagger Editor](https://editor.swagger.io/) para gerar PDF

---

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **PostgreSQL** (produção/staging)
- **Lombok**
- **Swagger/OpenAPI 3**
- **JUnit 5**
- **AssertJ**
- **JaCoCo** (cobertura de testes)
- **PMD** (análise estática)
- **Cucumber** (BDD)
- **Docker**
- **Jenkins**

---

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/subscription/
│   │       ├── application/          # Camada de aplicação
│   │       │   ├── dto/             # DTOs
│   │       │   └── service/         # Services
│   │       ├── config/              # Configurações
│   │       ├── domain/              # Domínio (DDD)
│   │       │   ├── constant/        # Constantes de negócio
│   │       │   ├── entity/          # Entidades
│   │       │   ├── exception/       # Exceções de domínio
│   │       │   ├── strategy/        # Strategy Pattern
│   │       │   └── valueobject/     # Value Objects
│   │       ├── infrastructure/      # Infraestrutura
│   │       │   └── repository/      # Repositories
│   │       ├── presentation/        # Apresentação
│   │       │   └── controller/      # Controllers
│   │       └── SubscriptionApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── application-staging.properties
│       └── ...
└── test/
    ├── java/                        # Testes unitários e de integração
    └── resources/
        ├── application-test.properties
        └── features/                # Features Cucumber (BDD)
```

---

## 🚀 Executando a Aplicação

### Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker (opcional, para PostgreSQL)

### Desenvolvimento Local (H2)

1. **Clone o repositório**:
```bash
git clone <repository-url>
cd Pratica1-Jekins
```

2. **Compile o projeto**:
```bash
mvn clean install
```

3. **Execute a aplicação**:
```bash
mvn spring-boot:run
```

4. **Acesse a aplicação**:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

### Produção/Staging (PostgreSQL)

1. **Inicie o PostgreSQL via Docker**:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

2. **Execute a aplicação com profile prod**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes com Cobertura (JaCoCo)

```bash
mvn clean test jacoco:report
```

Relatório disponível em: `target/site/jacoco/index.html`

### Executar Análise Estática (PMD)

```bash
mvn pmd:pmd
```

Relatório disponível em: `target/pmd.xml`

### Quality Gate (99% de Cobertura)

```bash
mvn jacoco:check
```

### Tipos de Testes

1. **Testes Unitários**:
   - Entity Tests (`StudentTest.java`)
   - Value Object Tests (`CreditsTest.java`, `CourseAverageTest.java`)
   - Repository Tests (`StudentRepositoryTest.java`)
   - Service Tests (`StudentServiceTest.java`, `GamificationServiceTest.java`)
   - Controller Tests (`StudentControllerTest.java`, `GamificationControllerTest.java`)

2. **Testes de Integração**:
   - `GamificationIntegrationTest.java`

3. **Testes BDD (Cucumber)**:
   - Features: `gamification.feature`, `student_api.feature`

### Anotações Utilizadas nos Testes

- `@DataJpaTest` - Testes de repository
- `@Mock` - Mock de dependências
- `@InjectMocks` - Injeção de mocks
- `@MockMvc` - Testes de controller
- `@SpringBootTest` - Testes de integração

---

## 🐳 Docker

### Dockerfile

O Dockerfile está configurado para:
- Usar Java 17 (OpenJDK)
- Copiar JAR para `/subscription-service/app.jar`
- Expor porta 8080
- Executar aplicação via `java -jar`

### Docker Compose

#### Produção (`docker-compose.prod.yml`)

```bash
docker-compose -f docker-compose.prod.yml up -d
```

- Porta: `8585:8080`
- Banco: PostgreSQL (`papi`)
- Profile: `prod`

#### Staging (`docker-compose.staging.yml`)

```bash
docker-compose -f docker-compose.staging.yml up -d
```

- Porta: `8686:8080`
- Banco: PostgreSQL (`sapi`)
- Profile: `staging`

### Build da Imagem Docker

```bash
docker build -t rickelmedias/subscription-service:latest .
```

### Push para Docker Hub

```bash
docker login
docker push rickelmedias/subscription-service:latest
```

---

## 🔄 Jenkins Pipeline

### Pipelines Disponíveis

#### 1. Pipeline DEV (`Jenkinsfile`)

**Objetivo**: Executar testes, análises e verificar Quality Gate.

**Stages**:
1. **Checkout** - Clona repositório
2. **Build** - Compila aplicação
3. **Unit Tests** - Executa testes unitários (JUnit)
4. **Code Analysis - PMD** - Análise estática
5. **Code Coverage - JaCoCo** - Gera relatório de cobertura
6. **Quality Gate** - Verifica 99% de cobertura
7. **Package** - Empacota JAR (apenas se Quality Gate passar)

**Relatórios Gerados**:
- JUnit: `target/surefire-reports/`
- PMD: `target/pmd.xml`
- JaCoCo: `target/site/jacoco/`

#### 2. Pipeline TEST-DEV (`Jenkinsfile.test-dev`)

**Objetivo**: Executar testes e análises (sub-pipeline do DEV).

**Stages**: Similar ao Pipeline DEV, focado em testes.

#### 3. Pipeline IMAGE_DOCKER (`Jenkinsfile.image-docker`)

**Objetivo**: Construir e publicar imagem Docker.

**Trigger**: Apenas se Quality Gate passar (99% de cobertura).

**Stages**:
1. **Checkout** - Clona repositório
2. **Build JAR** - Compila e empacota
3. **Build Docker Image** - Constrói imagem
4. **Push Docker Image** - Publica no Docker Hub

**Configuração**:
- Credenciais Docker Hub: `docker-hub-credentials`
- Imagem: `rickelmedias/subscription-service:latest`

#### 4. Pipeline PROD (`Jenkinsfile.prod`)

**Objetivo**: Deploy em produção.

**Stages**:
1. **Checkout** - Clona repositório
2. **Start container** - Baixa imagem do Docker Hub e inicia container
3. **Run tests against the container** - Testa saúde da aplicação

**Porta**: `8585`

#### 5. Pipeline STAGING (`Jenkinsfile.staging`)

**Objetivo**: Deploy em staging.

**Stages**: Similar ao Pipeline PROD.

**Porta**: `8686`

### Configuração do Jenkins

#### Pré-requisitos

1. **Ferramentas configuradas**:
   - Maven 3.9
   - JDK 17

2. **Plugins necessários**:
   - Pipeline
   - JUnit
   - JaCoCo
   - PMD
   - Docker Pipeline

3. **Credenciais**:
   - Docker Hub: `docker-hub-credentials` (username/password)

#### Quality Gate (99%)

O Quality Gate verifica:
- Cobertura de instruções: >= 99%
- Cobertura de branches: >= 99%

**Configuração no `pom.xml`**:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.99</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.99</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### Relatórios Gerados

#### Pre-Build
- Nenhum relatório gerado

#### Build
- **Compilação**: Logs do Maven
- **PMD**: `target/pmd.xml`

#### Post-Build
- **JUnit**: `target/surefire-reports/*.xml`
- **JaCoCo**: `target/site/jacoco/index.html`
- **PMD**: `target/pmd.xml`

### Workflow de Pipelines

```
1. Pipeline DEV (Jenkinsfile)
   ↓
2. Quality Gate (99% cobertura)
   ↓
3a. Se PASSAR → Pipeline IMAGE_DOCKER
   ↓
   3b. Build e Push Docker Image
   ↓
4. Pipeline PROD ou STAGING
   ↓
5. Deploy em container
```

---

## 📚 Documentação da API

### Endpoints Disponíveis

#### Estudantes

**GET /students**
- Lista todos os estudantes
- Resposta: `200 OK` com lista de `StudentDTO`

**GET /students/{id}**
- Busca estudante por ID
- Resposta: `200 OK` com `StudentDTO` ou `404 Not Found`

#### Gamificação

**POST /gamification/students/{id}/complete-course**
- Completa curso e aplica gamificação
- Body: `CourseCompletionRequestDTO` (campo `average`)
- Resposta: `200 OK` com `StudentDTO` atualizado

### Exemplos de Requisições

#### Listar Estudantes
```bash
curl http://localhost:8080/students
```

#### Buscar Estudante
```bash
curl http://localhost:8080/students/1
```

#### Completar Curso
```bash
curl -X POST http://localhost:8080/gamification/students/1/complete-course \
  -H "Content-Type: application/json" \
  -d '{"average": 8.5}'
```

### Swagger UI

Acesse `http://localhost:8080/swagger-ui.html` para documentação interativa.

---

## 🔍 Resumo das Correções Realizadas

### 1. ✅ Camada Entity (Entidades + Value Objects)
- Uso de Lombok (`@Getter`, `@Builder`, `@NoArgsConstructor`)
- Value Objects imutáveis (`Credits`, `CourseAverage`)
- Encapsulamento de lógica de negócio

### 2. ✅ Camada Repository e Padrão JPA
- Repository com Spring Data JPA
- Query methods corretos para campos embeddados
- Testes com `@DataJpaTest`

### 3. ✅ Configurações de Profiles
- `application.properties` (H2 - desenvolvimento)
- `application-prod.properties` (PostgreSQL - produção)
- `application-staging.properties` (PostgreSQL - staging)
- `application-test.properties` (H2 - testes)

### 4. ✅ Gerar Schema a partir do ORM
- H2 Console habilitado: `http://localhost:8080/h2-console`
- JPA `ddl-auto=update` para gerar schema

### 5. ✅ Camada de DTO
- `StudentDTO` com mapeamento Entity → DTO
- `CourseCompletionRequestDTO` com validações

### 6. ✅ Camada Service
- `StudentService` para CRUD
- `GamificationService` para lógica de gamificação
- Transações (`@Transactional`)

### 7. ✅ Camada Controller
- `StudentController` para endpoints de estudantes
- `GamificationController` para endpoints de gamificação
- `GlobalExceptionHandler` para tratamento de exceções

### 8. ✅ Configuração Swagger
- `OpenApiConfig` para documentação
- Endpoints documentados com anotações Swagger

### 9. ✅ Jenkins Pipeline
- Pipeline DEV com testes e análises
- Pipeline TEST-DEV (sub-pipeline)
- Pipeline IMAGE_DOCKER (trigger após Quality Gate)
- Pipeline PROD e STAGING para deploy

### 10. ✅ Quality Gate 99%
- Configuração JaCoCo para 99% de cobertura
- Verificação no pipeline
- Falha do build se não atingir 99%

### 11. ✅ Imagem Docker
- Dockerfile configurado
- Build apenas se Quality Gate passar
- Push para Docker Hub

### 12. ✅ Pipelines com Trigger
- Pipeline DEV → TEST-DEV
- TEST-DEV → IMAGE_DOCKER (se Quality Gate passar)

### 13. ✅ Testes das Camadas
- Testes unitários (Entity, Repository, Service, Controller)
- Uso de `@DataJpaTest`, `@Mock`, `@InjectMocks`, `@MockMvc`
- Testes de integração

### 14. ✅ Arquivos DevOps
- `Dockerfile`
- `docker-compose.prod.yml`
- `docker-compose.staging.yml`
- `Jenkinsfile`, `Jenkinsfile.test-dev`, `Jenkinsfile.image-docker`
- `Jenkinsfile.prod`, `Jenkinsfile.staging`

---

## 📝 Notas Adicionais

### Estrutura DDD

O projeto segue Domain-Driven Design:
- **Domain**: Lógica de negócio pura (Entity, Value Objects, Services)
- **Application**: Casos de uso (Services, DTOs)
- **Infrastructure**: Implementações técnicas (Repository, JPA)
- **Presentation**: Interfaces externas (Controllers, REST)

### Princípios SOLID

- **Single Responsibility**: Cada classe tem uma responsabilidade única
- **Open/Closed**: Extensível via Strategy Pattern
- **Liskov Substitution**: Value Objects são substituíveis
- **Interface Segregation**: Interfaces específicas
- **Dependency Inversion**: Dependências de abstrações

### Padrões de Design

- **Repository Pattern**: Abstração de acesso a dados
- **Strategy Pattern**: Cálculo de créditos (Standard, Premium)
- **DTO Pattern**: Transferência de dados
- **Builder Pattern**: Construção de entidades

---

## 👥 Autores

- Desenvolvido seguindo exemplo do projeto AC2_CA
- Adaptado para projeto Subscription & Gamification

## 📄 Licença

Este projeto é um exemplo educacional.

---

## 🆘 Suporte

Para dúvidas ou problemas:
1. Verifique os logs da aplicação
2. Consulte a documentação Swagger
3. Execute os testes para validar funcionalidades

---

**Última atualização**: 2024
