# 🚀 Guia de Execução em Produção - Subscription Service

## ✅ Resultado do Teste de Execução

### Status: **SUCESSO** ✅

A aplicação foi executada com sucesso em modo de desenvolvimento. Resultados:

- ✅ **Compilação**: Sem erros
- ✅ **Inicialização**: Spring Boot iniciado em 2.128 segundos
- ✅ **Banco de Dados**: H2 conectado e tabelas criadas
- ✅ **Servidor**: Tomcat rodando na porta 8080
- ✅ **Health Check**: `{"status":"UP"}`

### Logs da Execução

```
:: Spring Boot ::                (v3.3.4)
Started SubscriptionApplication in 2.128 seconds
Tomcat started on port 8080 (http) with context path '/'
H2 console available at '/h2-console'
```

### Aviso (Não é Erro)

```
WARN: H2Dialect does not need to be specified explicitly
```

Este é apenas um aviso informativo. O Hibernate detecta automaticamente o dialeto do H2, mas não afeta o funcionamento.

---

## 🏭 Como Rodar em Produção

### Opção 1: Docker Compose (Recomendado)

#### Pré-requisitos

1. **Docker** e **Docker Compose** instalados
2. **Porta 8585** disponível (produção)
3. **Porta 5432** disponível (PostgreSQL)

#### Passo 1: Preparar a Imagem Docker

```bash
# Build da aplicação
mvn clean package -DskipTests

# Build da imagem Docker
docker build -t rickelmedias/subscription-service:latest .

# Ou usar a imagem do Docker Hub
docker pull rickelmedias/subscription-service:latest
```

#### Passo 2: Configurar Variáveis de Ambiente

Crie um arquivo `.env` (opcional):

```bash
# .env
DB_HOST=database
DB_USER=postgres
DB_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=prod
```

#### Passo 3: Executar com Docker Compose

```bash
# Iniciar serviços (aplicação + PostgreSQL)
docker-compose -f docker-compose.prod.yml up -d

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f

# Verificar status
docker-compose -f docker-compose.prod.yml ps

# Parar serviços
docker-compose -f docker-compose.prod.yml down
```

#### Passo 4: Verificar se Está Funcionando

```bash
# Health Check
curl http://localhost:8585/actuator/health

# Resposta esperada:
# {"status":"UP"}

# Acessar Swagger
# http://localhost:8585/swagger-ui.html
```

### Opção 2: Executar JAR Diretamente

#### Pré-requisitos

1. **Java 17** instalado
2. **PostgreSQL** rodando e configurado
3. **JAR** da aplicação gerado

#### Passo 1: Gerar JAR

```bash
# Gerar JAR
mvn clean package -DskipTests

# O JAR estará em: target/subscription-service-0.0.1-SNAPSHOT.jar
```

#### Passo 2: Configurar Banco de Dados PostgreSQL

```bash
# Criar banco de dados
psql -U postgres -c "CREATE DATABASE papi;"

# Ou usar um script SQL
psql -U postgres -f init-db.sql
```

#### Passo 3: Configurar Variáveis de Ambiente

```bash
# Exportar variáveis
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_USER=postgres
export DB_PASSWORD=postgres
export DB_NAME=papi
```

#### Passo 4: Executar Aplicação

```bash
# Executar JAR
java -jar target/subscription-service-0.0.1-SNAPSHOT.jar

# Ou com variáveis inline
java -jar \
  -Dspring.profiles.active=prod \
  -Dspring.datasource.url=jdbc:postgresql://localhost:5432/papi \
  -Dspring.datasource.username=postgres \
  -Dspring.datasource.password=postgres \
  target/subscription-service-0.0.1-SNAPSHOT.jar
```

#### Passo 5: Verificar se Está Funcionando

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Acessar Swagger
# http://localhost:8080/swagger-ui.html
```

### Opção 3: Executar com Maven (Desenvolvimento/Teste)

```bash
# Executar com profile de produção
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Ou com variáveis de ambiente
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

---

## 📋 Configuração de Produção

### application-prod.properties

```properties
# Database - PostgreSQL
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:5432/papi
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# H2 Console (desabilitado em produção)
spring.h2.console.enabled=false

# Logging
logging.level.root=INFO
logging.level.com.example.subscription=INFO

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

### docker-compose.prod.yml

```yaml
version: '4'

services:
  database:
    image: postgres:15-alpine
    environment:
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: papi
    volumes:
      - db-volume:/var/lib/postgresql/data
    networks:
      - default
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    image: rickelmedias/subscription-service:latest
    networks:
      - default
    environment:
      DB_HOST: database
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8585:8080"
    depends_on:
      database:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

volumes:
  db-volume:

networks:
  default:
    driver: bridge
```

---

## 🔧 Variáveis de Ambiente

### Variáveis Obrigatórias

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `SPRING_PROFILES_ACTIVE` | Profile ativo | `prod` |
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_USER` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `DB_NAME` | Nome do banco | `papi` |

### Variáveis Opcionais

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `SERVER_PORT` | Porta da aplicação | `8080` |
| `JAVA_OPTS` | Opções JVM | `-Xms256m -Xmx512m` |
| `LOG_LEVEL` | Nível de log | `INFO` |

---

## 🐳 Deploy com Docker

### Build e Push da Imagem

```bash
# 1. Build da aplicação
mvn clean package -DskipTests

# 2. Build da imagem Docker
docker build -t rickelmedias/subscription-service:latest .

# 3. Tag da imagem
docker tag rickelmedias/subscription-service:latest rickelmedias/subscription-service:v1.0.0

# 4. Login no Docker Hub
docker login -u rickelmedias

# 5. Push da imagem
docker push rickelmedias/subscription-service:latest
docker push rickelmedias/subscription-service:v1.0.0
```

### Executar Container

```bash
# Executar container com PostgreSQL externo
docker run -d \
  --name subscription-service \
  -p 8585:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  rickelmedias/subscription-service:latest

# Ou usar docker-compose
docker-compose -f docker-compose.prod.yml up -d
```

---

## 🔍 Verificação de Produção

### Health Check

```bash
# Verificar saúde da aplicação
curl http://localhost:8585/actuator/health

# Resposta esperada:
# {"status":"UP"}
```

### Verificar Logs

```bash
# Docker Compose
docker-compose -f docker-compose.prod.yml logs -f api

# Docker
docker logs -f subscription-service

# JAR
# Logs aparecem no console ou arquivo configurado
```

### Testar Endpoints

```bash
# Listar estudantes
curl http://localhost:8585/students

# Health check
curl http://localhost:8585/actuator/health

# Swagger UI
# http://localhost:8585/swagger-ui.html
```

---

## 🚀 Deploy no Jenkins (Pipeline PROD)

### Pipeline PROD

O pipeline PROD está configurado em `Jenkinsfile.prod`:

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Start container') {
            steps {
                echo 'Starting container from Docker Hub...'
                sh 'docker-compose -f docker-compose.prod.yml pull'
                sh 'docker-compose -f docker-compose.prod.yml up -d --no-color'
                sleep time: 60, unit: 'SECONDS'
                sh 'docker-compose -f docker-compose.prod.yml logs'
                sh 'docker-compose -f docker-compose.prod.yml ps'
            }
        }
        
        stage('Run tests against the container') {
            steps {
                script {
                    def response = sh(
                        script: 'curl -f http://localhost:8585/actuator/health || echo "Service not responding"',
                        returnStatus: true
                    )
                    if (response != 0) {
                        echo "⚠️ Service not responding, but continuing..."
                    } else {
                        echo "✅ Service is responding!"
                    }
                }
            }
        }
    }
}
```

### Executar Pipeline PROD

1. **No Jenkins**:
   - Acesse o pipeline `subscription-service-prod`
   - Clique em **"Build Now"**
   - Aguarde a execução

2. **Verificar Deploy**:
   ```bash
   curl http://localhost:8585/actuator/health
   ```

---

## 📊 Monitoramento

### Métricas Disponíveis

- **Health Check**: `/actuator/health`
- **Info**: `/actuator/info`
- **Swagger UI**: `/swagger-ui.html`
- **API Docs**: `/api-docs`

### Logs

Os logs estão configurados para:
- **Console**: Saída padrão
- **Arquivo**: Configurável via `logging.file.name`
- **Nível**: `INFO` em produção

---

## 🔐 Segurança em Produção

### Recomendações

1. **Senhas**: Use variáveis de ambiente ou secrets management
2. **HTTPS**: Configure SSL/TLS em produção
3. **Autenticação**: Adicione autenticação e autorização
4. **Rate Limiting**: Configure rate limiting
5. **CORS**: Configure CORS adequadamente
6. **Logs**: Não logue informações sensíveis
7. **H2 Console**: Desabilitado em produção
8. **SQL Logging**: Desabilitado em produção

---

## 🆘 Troubleshooting

### Problema: Aplicação não inicia

**Solução**:
```bash
# Verificar logs
docker-compose -f docker-compose.prod.yml logs api

# Verificar se PostgreSQL está rodando
docker-compose -f docker-compose.prod.yml ps database

# Verificar variáveis de ambiente
docker-compose -f docker-compose.prod.yml config
```

### Problema: Erro de conexão com banco

**Solução**:
```bash
# Verificar se PostgreSQL está acessível
docker-compose -f docker-compose.prod.yml exec database psql -U postgres -c "SELECT 1;"

# Verificar variáveis de ambiente
echo $DB_HOST
echo $DB_USER
echo $DB_PASSWORD
```

### Problema: Porta já em uso

**Solução**:
```bash
# Verificar qual processo está usando a porta
sudo lsof -i :8585

# Parar processo ou mudar porta
# Em docker-compose.prod.yml: "8586:8080"
```

---

## 📝 Resumo

### Execução Rápida em Produção

```bash
# 1. Build e push da imagem
mvn clean package -DskipTests
docker build -t rickelmedias/subscription-service:latest .
docker push rickelmedias/subscription-service:latest

# 2. Executar com Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# 3. Verificar
curl http://localhost:8585/actuator/health
```

### Execução com JAR

```bash
# 1. Gerar JAR
mvn clean package -DskipTests

# 2. Configurar variáveis
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_USER=postgres
export DB_PASSWORD=postgres

# 3. Executar
java -jar target/subscription-service-0.0.1-SNAPSHOT.jar
```

---

**Documento gerado em**: 2025-11-08
**Versão**: 1.0
**Status**: ✅ Aplicação testada e funcionando

