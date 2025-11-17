# 🚀 Guia Completo: Jenkins com Docker - Pipeline DEV, Staging e PROD

Este guia vai te levar do zero até ter um ambiente Jenkins completamente funcional com pipelines para desenvolvimento, staging e produção, incluindo build, testes, análise de código, cobertura, qualidade e deploy com Docker.

---

## 📋 Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Criação de Conta no Docker Hub](#criação-de-conta-no-docker-hub)
3. [Instalação do Jenkins via Docker](#instalação-do-jenkins-via-docker)
4. [Configuração Inicial do Jenkins](#configuração-inicial-do-jenkins)
5. [Instalação de Plugins](#instalação-de-plugins)
6. [Configuração de Ferramentas](#configuração-de-ferramentas)
7. [Configuração de Credenciais](#configuração-de-credenciais)
8. [Criação dos Pipelines](#criação-dos-pipelines)
9. [Testando os Pipelines](#testando-os-pipelines)
10. [Troubleshooting](#troubleshooting)

---

## 🔧 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Docker** (versão 20.10 ou superior)
- **Docker Compose** (versão 2.0 ou superior)
- **Git** (para clonar o repositório)
- **Navegador web** (Chrome, Firefox, Edge, etc.)
- **Portas disponíveis**:
  - `8080` (Jenkins)
  - `8585` (Produção)
  - `8686` (Staging)
  - `5432` (PostgreSQL, se necessário)

### Verificando Instalações

```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version

# Verificar Git
git --version
```

---

## 🐳 Criação de Conta no Docker Hub

### Passo 1: Acesse o Docker Hub

1. Acesse [https://hub.docker.com/](https://hub.docker.com/)
2. Clique em **"Sign Up"** (Criar conta)

### Passo 2: Preencha os Dados

- **Username**: `rickelmedias` (ou o username que você escolher)
- **Email**: Seu email válido
- **Password**: Crie uma senha forte
- Aceite os termos de serviço

### Passo 3: Verifique seu Email

1. Verifique sua caixa de entrada
2. Clique no link de confirmação enviado pelo Docker Hub

### Passo 4: Faça Login

```bash
# No terminal, faça login no Docker Hub
docker login

# Digite seu username: rickelmedias
# Digite sua senha
```

### Passo 5: Criar Repositório (Opcional)

O repositório será criado automaticamente quando você fizer o primeiro push da imagem. Mas você pode criar manualmente:

1. Acesse [https://hub.docker.com/repositories](https://hub.docker.com/repositories)
2. Clique em **"Create Repository"**
3. Nome: `subscription-service`
4. Visibilidade: **Public** (ou Private, conforme preferência)
5. Clique em **"Create"**

**Importante**: O nome da imagem será `rickelmedias/subscription-service` (username/repository).

---

## 📦 Instalação do Jenkins via Docker

### Passo 1: Criar Diretório para Jenkins

```bash
# Criar diretório para dados do Jenkins
mkdir -p ~/jenkins_home

# Dar permissões (importante!)
sudo chown -R 1000:1000 ~/jenkins_home
```

### Passo 2: Executar Jenkins em Container

**IMPORTANTE**: Para que o Jenkins consiga executar comandos Docker (necessário para build de imagens), precisamos montar o socket do Docker e o binário.

```bash
# Executar Jenkins com Docker
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  --group-add $(getent group docker | cut -d: -f3) \
  --restart unless-stopped \
  jenkins/jenkins:lts
```

**Alternativa (se o comando acima não funcionar)**:

```bash
# Método alternativo - adicionar usuário ao grupo docker
sudo groupadd docker 2>/dev/null || true
sudo usermod -aG docker $USER

# Executar Jenkins
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v $(which docker):/usr/bin/docker \
  --restart unless-stopped \
  jenkins/jenkins:lts
```

**Explicação dos parâmetros:**
- `-d`: Executa em background
- `--name jenkins`: Nome do container
- `-p 8080:8080`: Porta HTTP do Jenkins
- `-p 50000:50000`: Porta para agentes JNLP
- `-v ~/jenkins_home:/var/jenkins_home`: Persistência de dados
- `-v /var/run/docker.sock:/var/run/docker.sock`: Permite usar Docker dentro do Jenkins (Docker-in-Docker)
- `-v /usr/bin/docker:/usr/bin/docker`: Permite executar comandos Docker
- `--group-add`: Adiciona o container ao grupo docker (permite acessar o socket)
- `--restart unless-stopped`: Reinicia automaticamente

**Nota**: Se você encontrar problemas de permissão com Docker, veja a seção [Troubleshooting](#troubleshooting).

### Passo 3: Verificar se Jenkins está Rodando

```bash
# Ver logs do Jenkins
docker logs jenkins

# Verificar status
docker ps | grep jenkins
```

### Passo 4: Obter Senha Inicial

```bash
# Obter senha inicial do Jenkins
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

**Copie a senha** - você vai precisar dela no próximo passo!

---

## 🔐 Configuração Inicial do Jenkins

### Passo 1: Acessar Jenkins

1. Abra seu navegador
2. Acesse: `http://localhost:8080`
3. Cole a senha inicial que você copiou
4. Clique em **"Continue"**

### Passo 2: Instalar Plugins Sugeridos

1. Na tela **"Customize Jenkins"**, selecione **"Install suggested plugins"**
2. Aguarde a instalação dos plugins (pode demorar alguns minutos)
3. Após a instalação, clique em **"Continue"**

### Passo 3: Criar Usuário Administrador

1. Preencha os dados:
   - **Username**: `admin` (ou o que preferir)
   - **Password**: Crie uma senha forte
   - **Full name**: Seu nome
   - **Email**: Seu email
2. Clique em **"Save and Continue"**

### Passo 4: Configurar URL do Jenkins

1. Na tela **"Instance Configuration"**, mantenha a URL padrão: `http://localhost:8080/`
2. Clique em **"Save and Finish"**

### Passo 5: Finalizar

1. Clique em **"Start using Jenkins"**
2. Você será redirecionado para o dashboard do Jenkins

---

## 🔌 Instalação de Plugins

Agora vamos instalar os plugins necessários para nossos pipelines.

### Passo 1: Acessar Gerenciador de Plugins

1. No dashboard do Jenkins, clique em **"Manage Jenkins"** (no menu lateral)
2. Clique em **"Plugins"** (ou **"Manage Plugins"**)
3. Vá para a aba **"Available"**

### Passo 2: Instalar Plugins Essenciais

Procure e marque os seguintes plugins (use a barra de busca):

#### Plugins Obrigatórios:

1. **Pipeline** (geralmente já instalado)
2. **Docker Pipeline** - Para construir imagens Docker
3. **Docker** - Integração com Docker
4. **Blue Ocean** - Interface moderna (opcional, mas recomendado)
5. **Git** - Integração com Git (geralmente já instalado)
6. **Maven Integration** - Integração com Maven
7. **JaCoCo** - Relatórios de cobertura de código
8. **Warnings Next Generation** - Análise de código (PMD, etc.) - **IMPORTANTE para PMD**
9. **HTML Publisher** - Publicar relatórios HTML
10. **Credentials Binding** - Gerenciar credenciais
11. **JUnit** - Relatórios de testes JUnit (para testes unitários)

#### Plugins Adicionais Recomendados:

12. **Timestamper** - Timestamps nos logs
13. **AnsiColor** - Cores nos logs
14. **Build Timeout** - Timeout para builds
15. **Workspace Cleanup** - Limpar workspace
16. **GitHub Integration** - Se usar GitHub (para webhooks)
17. **GitLab Plugin** - Se usar GitLab (para webhooks)
18. **Pipeline Stage View** - Visualização de estágios do pipeline

**Nota**: Se algum plugin não aparecer na busca, ele pode já estar instalado ou ter um nome ligeiramente diferente.

### Passo 3: Instalar os Plugins

1. Marque todos os plugins listados acima
2. Clique em **"Install without restart"** (ou **"Download now and install after restart"**)
3. Aguarde a instalação
4. Se solicitado, marque **"Restart Jenkins when installation is complete and no jobs are running"**

### Passo 4: Verificar Instalação

1. Após reiniciar, acesse: `http://localhost:8080`
2. Vá em **"Manage Jenkins"** > **"Plugins"** > **"Installed"**
3. Verifique se todos os plugins estão instalados

---

## 🛠️ Configuração de Ferramentas

Agora vamos configurar o Maven e o JDK no Jenkins.

### Passo 1: Acessar Configuração de Ferramentas

1. No dashboard, clique em **"Manage Jenkins"**
2. Clique em **"Tools"** (ou **"Global Tool Configuration"**)

### Passo 2: Configurar JDK 17

1. Na seção **"JDK"**, clique em **"Add JDK"**
2. **Name**: `JDK-17`
3. Marque **"Install automatically"**
4. **Install from**: Selecione `adoptium-jdk`
5. **Version**: Selecione `jdk-17.0.x-latest` (ou a versão mais recente disponível)
6. Clique em **"Save"**

### Passo 3: Configurar Maven 3.9

1. Na seção **"Maven"**, clique em **"Add Maven"**
2. **Name**: `Maven-3.9`
3. Marque **"Install automatically"**
4. **Version**: Selecione `3.9.5` (ou a versão mais recente disponível)
5. Clique em **"Save"**

### Passo 4: Salvar Configurações

1. Role até o final da página
2. Clique em **"Save"**

**Nota**: Se você já tem JDK e Maven instalados localmente, pode configurar os caminhos manualmente em vez de instalação automática.

---

## 🔑 Configuração de Credenciais

Agora vamos configurar as credenciais do Docker Hub para fazer push das imagens.

### Passo 1: Acessar Gerenciador de Credenciais

1. No dashboard, clique em **"Manage Jenkins"**
2. Clique em **"Credentials"**
3. Clique em **"System"** (ou **"Global"**)
4. Clique em **"Add Credentials"**

### Passo 2: Configurar Credenciais do Docker Hub

1. **Kind**: Selecione `Username with password`
2. **Scope**: Selecione `Global`
3. **Username**: `rickelmedias` (seu username do Docker Hub)
4. **Password**: Sua senha do Docker Hub
5. **ID**: `docker-hub-credentials` (importante: use exatamente este ID)
6. **Description**: `Docker Hub Credentials for rickelmedias`
7. Clique em **"Create"**

### Passo 3: Verificar Credenciais

1. Você deve ver as credenciais listadas em **"Global credentials"**
2. O ID deve ser exatamente: `docker-hub-credentials`

**Importante**: O ID `docker-hub-credentials` é usado nos pipelines. Se você mudar, atualize os Jenkinsfiles também.

---

## 🚀 Criação dos Pipelines

Agora vamos criar os pipelines. Vamos criar 4 pipelines principais:

1. **DEV** - Build, testes, análise e qualidade
2. **TEST-DEV** - Testes e análises
3. **IMAGE-DOCKER** - Build e push da imagem Docker
4. **STAGING** - Deploy em staging
5. **PROD** - Deploy em produção

### Passo 1: Criar Pipeline DEV

1. No dashboard, clique em **"New Item"**
2. **Item name**: `subscription-service-dev`
3. Selecione **"Pipeline"**
4. Clique em **"OK"**

### Passo 2: Configurar Pipeline DEV

1. **Description**: `Pipeline DEV - Build, testes, análise e qualidade`
2. Na seção **"Pipeline"**:
   - **Definition**: Selecione `Pipeline script from SCM`
   - **SCM**: Selecione `Git`
   - **Repository URL**: URL do seu repositório Git (ex: `https://github.com/seu-usuario/seu-repo.git`)
   - **Credentials**: Se necessário, adicione credenciais do Git
   - **Branch Specifier**: `*/main` ou `*/master` (ajuste conforme sua branch)
   - **Script Path**: `Jenkinsfile`
3. Clique em **"Save"**

### Passo 3: Criar Pipeline TEST-DEV

1. Clique em **"New Item"**
2. **Item name**: `subscription-service-test-dev`
3. Selecione **"Pipeline"**
4. Clique em **"OK"**
5. Configure:
   - **Description**: `Pipeline TEST-DEV - Testes e análises`
   - **Pipeline Definition**: `Pipeline script from SCM`
   - **Repository URL**: Mesma URL do repositório
   - **Script Path**: `Jenkinsfile.test-dev`
6. Clique em **"Save"**

### Passo 4: Criar Pipeline IMAGE-DOCKER

1. Clique em **"New Item"**
2. **Item name**: `subscription-service-image-docker`
3. Selecione **"Pipeline"**
4. Clique em **"OK"**
5. Configure:
   - **Description**: `Pipeline IMAGE-DOCKER - Build e push da imagem Docker`
   - **Pipeline Definition**: `Pipeline script from SCM`
   - **Repository URL**: Mesma URL do repositório
   - **Script Path**: `Jenkinsfile.image-docker`
6. Clique em **"Save"**

### Passo 5: Criar Pipeline STAGING

1. Clique em **"New Item"**
2. **Item name**: `subscription-service-staging`
3. Selecione **"Pipeline"**
4. Clique em **"OK"**
5. Configure:
   - **Description**: `Pipeline STAGING - Deploy em ambiente de staging`
   - **Pipeline Definition**: `Pipeline script from SCM`
   - **Repository URL**: Mesma URL do repositório
   - **Script Path**: `Jenkinsfile.staging`
6. Clique em **"Save"**

### Passo 6: Criar Pipeline PROD

1. Clique em **"New Item"**
2. **Item name**: `subscription-service-prod`
3. Selecione **"Pipeline"**
4. Clique em **"OK"**
5. Configure:
   - **Description**: `Pipeline PROD - Deploy em ambiente de produção`
   - **Pipeline Definition**: `Pipeline script from SCM`
   - **Repository URL**: Mesma URL do repositório
   - **Script Path**: `Jenkinsfile.prod`
6. Clique em **"Save"**

---

## 🧪 Testando Localmente (Antes do Jenkins)

Antes de testar no Jenkins, é importante garantir que tudo funciona localmente.

### Passo 1: Testar Build Local

```bash
# Navegar para o diretório do projeto
cd /home/r1ddax/facens/leles/Pratica1-Jekins

# Limpar e compilar
mvn clean compile

# Executar testes
mvn test

# Gerar relatório de cobertura
mvn jacoco:report

# Verificar qualidade (deve passar com 99% de cobertura)
mvn jacoco:check

# Análise PMD
mvn pmd:pmd

# Empacotar
mvn package -DskipTests
```

### Passo 2: Testar Build da Imagem Docker

```bash
# Build da imagem
docker build -t rickelmedias/subscription-service:latest .

# Testar a imagem localmente
docker run -d -p 8080:8080 --name test-subscription rickelmedias/subscription-service:latest

# Verificar se está rodando
curl http://localhost:8080/actuator/health

# Parar e remover o container de teste
docker stop test-subscription
docker rm test-subscription
```

### Passo 3: Testar Push para Docker Hub

```bash
# Fazer login no Docker Hub
docker login -u rickelmedias

# Fazer tag da imagem
docker tag rickelmedias/subscription-service:latest rickelmedias/subscription-service:test

# Push (teste com uma tag de teste primeiro)
docker push rickelmedias/subscription-service:test

# Verificar no Docker Hub
# Acesse: https://hub.docker.com/r/rickelmedias/subscription-service
```

### Passo 4: Testar Docker Compose

```bash
# Testar staging
docker-compose -f docker-compose.staging.yml up -d

# Verificar logs
docker-compose -f docker-compose.staging.yml logs

# Verificar health
curl http://localhost:8686/actuator/health

# Parar
docker-compose -f docker-compose.staging.yml down

# Testar produção
docker-compose -f docker-compose.prod.yml up -d
curl http://localhost:8585/actuator/health
docker-compose -f docker-compose.prod.yml down
```

**Se todos esses testes passarem, você está pronto para usar o Jenkins!**

---

## 🧪 Testando os Pipelines

Agora vamos testar se tudo está funcionando corretamente no Jenkins.

### Passo 1: Testar Pipeline DEV

1. No dashboard, clique no pipeline `subscription-service-dev`
2. Clique em **"Build Now"**
3. Aguarde o build completar
4. Clique no build na lista (ex: `#1`)
5. Clique em **"Console Output"** para ver os logs

**O que esperar:**
- ✅ Checkout do repositório
- ✅ Compilação do projeto
- ✅ Execução de testes
- ✅ Análise PMD
- ✅ Relatório JaCoCo
- ✅ Quality Gate (99% de cobertura)
- ✅ Empacotamento do JAR

### Passo 2: Verificar Relatórios

1. No build, você verá links para:
   - **Test Result** - Resultados dos testes JUnit
   - **JaCoCo Coverage Report** - Cobertura de código
   - **PMD Warnings** - Análise estática de código
   - **Artifacts** - JAR gerado

### Passo 3: Testar Pipeline IMAGE-DOCKER

**Importante**: Execute este pipeline apenas após o DEV passar com sucesso.

1. Clique no pipeline `subscription-service-image-docker`
2. Clique em **"Build Now"**
3. Aguarde o build completar

**O que esperar:**
- ✅ Build do JAR
- ✅ Build da imagem Docker
- ✅ Push para Docker Hub

### Passo 4: Verificar Imagem no Docker Hub

1. Acesse [https://hub.docker.com/r/rickelmedias/subscription-service](https://hub.docker.com/r/rickelmedias/subscription-service)
2. Você deve ver a imagem `rickelmedias/subscription-service:latest`
3. Verifique as tags disponíveis

### Passo 5: Testar Pipeline STAGING

1. Clique no pipeline `subscription-service-staging`
2. Clique em **"Build Now"**
3. Aguarde o build completar

**O que esperar:**
- ✅ Pull da imagem do Docker Hub
- ✅ Inicialização do container
- ✅ Health check na porta 8686

### Passo 6: Testar Pipeline PROD

1. Clique no pipeline `subscription-service-prod`
2. Clique em **"Build Now"**
3. Aguarde o build completar

**O que esperar:**
- ✅ Pull da imagem do Docker Hub
- ✅ Inicialização do container
- ✅ Health check na porta 8585

---

## 🔍 Troubleshooting

### Problema 1: Jenkins não consegue executar Docker

**Sintoma**: Erro `docker: command not found` ou `Cannot connect to the Docker daemon`

**Solução 1 - Permissões do Docker Socket**:
```bash
# Verificar permissões do socket Docker
ls -la /var/run/docker.sock

# Adicionar permissões (temporário, para teste)
sudo chmod 666 /var/run/docker.sock

# Ou adicionar seu usuário ao grupo docker (permanente)
sudo usermod -aG docker $USER
newgrp docker

# Reiniciar Jenkins
docker restart jenkins
```

**Solução 2 - Instalar Docker dentro do Container Jenkins**:
```bash
# Parar o container Jenkins
docker stop jenkins
docker rm jenkins

# Executar com Docker instalado dentro do container
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --group-add $(stat -c %g /var/run/docker.sock) \
  --restart unless-stopped \
  jenkins/jenkins:lts
```

**Solução 3 - Usar Docker-in-Docker (DinD)**:
```bash
# Executar Jenkins com Docker-in-Docker
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  --privileged \
  docker:dind \
  jenkins/jenkins:lts
```

**Nota**: A Solução 1 é a mais recomendada para desenvolvimento local.

### Problema 2: Credenciais do Docker Hub não funcionam

**Sintoma**: Erro `unauthorized: authentication required` ao fazer push

**Solução**:
1. Verifique se o ID da credencial é exatamente `docker-hub-credentials`
2. Verifique se o username e senha estão corretos
3. Teste fazer login manualmente:
   ```bash
   docker login -u rickelmedias
   ```

### Problema 3: Maven ou JDK não encontrados

**Sintoma**: Erro `Maven-3.9 not found` ou `JDK-17 not found`

**Solução**:
1. Vá em **"Manage Jenkins"** > **"Tools"**
2. Verifique se Maven-3.9 e JDK-17 estão configurados
3. Verifique se os nomes nos pipelines estão exatamente iguais
4. Se necessário, reinstale as ferramentas

### Problema 4: Quality Gate falhando

**Sintoma**: Build falha no stage "Quality Gate"

**Solução**:
1. Verifique a cobertura de código nos relatórios JaCoCo
2. Se a cobertura estiver abaixo de 99%, você precisa aumentar os testes
3. Verifique as exclusões no `pom.xml` (pode estar excluindo código demais)

### Problema 5: Pipeline não encontra o Jenkinsfile

**Sintoma**: Erro `Unable to find Jenkinsfile`

**Solução**:
1. Verifique se o `Jenkinsfile` está na raiz do repositório
2. Verifique se o caminho no pipeline está correto (`Jenkinsfile`)
3. Verifique se o repositório Git está acessível
4. Teste fazer checkout manual do repositório

### Problema 6: Porta já em uso

**Sintoma**: Erro `port is already allocated` ao iniciar Jenkins

**Solução**:
```bash
# Verificar qual processo está usando a porta
sudo lsof -i :8080

# Parar o processo ou mudar a porta do Jenkins
docker run -d -p 8081:8080 ...  # Usar porta 8081 em vez de 8080
```

### Problema 7: Container não inicia (STAGING/PROD)

**Sintoma**: Container não inicia ou falha health check

**Solução**:
1. Verifique os logs:
   ```bash
   docker-compose -f docker-compose.staging.yml logs
   ```
2. Verifique se as portas estão disponíveis (8585, 8686)
3. Verifique se a imagem foi publicada no Docker Hub
4. Verifique as variáveis de ambiente nos arquivos docker-compose

### Problema 8: Permissões no Jenkins Home

**Sintoma**: Erros de permissão ao salvar configurações

**Solução**:
```bash
# Corrigir permissões
sudo chown -R 1000:1000 ~/jenkins_home
sudo chmod -R 755 ~/jenkins_home

# Se ainda tiver problemas, use 777 (menos seguro, mas funcional)
sudo chmod -R 777 ~/jenkins_home
```

### Problema 9: Plugin Warnings Next Generation não mostra PMD

**Sintoma**: PMD não aparece nos relatórios ou erros ao processar

**Solução**:
1. Verifique se o plugin **Warnings Next Generation** está instalado
2. Verifique se o arquivo `target/pmd.xml` está sendo gerado:
   ```bash
   mvn pmd:pmd
   ls -la target/pmd.xml
   ```
3. No pipeline, verifique se o padrão está correto: `**/target/pmd.xml`
4. Reinstale o plugin se necessário

### Problema 10: JaCoCo não gera relatório

**Sintoma**: Relatório JaCoCo não aparece ou está vazio

**Solução**:
1. Verifique se os testes foram executados:
   ```bash
   mvn test
   ls -la target/jacoco.exec
   ```
2. Verifique se o relatório foi gerado:
   ```bash
   mvn jacoco:report
   ls -la target/site/jacoco/
   ```
3. Verifique as exclusões no `pom.xml` (pode estar excluindo tudo)
4. Verifique os padrões no pipeline (execPattern, classPattern, etc.)

### Problema 11: Pipeline não encontra ferramentas (Maven, JDK)

**Sintoma**: Erro `Maven-3.9 not found` ou `JDK-17 not found`

**Solução**:
1. Vá em **Manage Jenkins** > **Tools**
2. Verifique se as ferramentas estão instaladas:
   - Clique em **"Maven"** e verifique se `Maven-3.9` existe
   - Clique em **"JDK"** e verifique se `JDK-17` existe
3. Se não existirem, adicione novamente
4. Verifique se os nomes no pipeline estão **exatamente** iguais:
   - No pipeline: `maven 'Maven-3.9'`
   - No Jenkins: Name deve ser `Maven-3.9` (case-sensitive)
5. Aguarde a instalação automática (pode demorar alguns minutos)

---

## 📊 Estrutura dos Pipelines

### Pipeline DEV (`Jenkinsfile`)

1. **Checkout** - Clona repositório
2. **Build** - Compila aplicação
3. **Unit Tests** - Executa testes unitários
4. **Code Analysis - PMD** - Análise estática
5. **Code Coverage - JaCoCo** - Gera relatório de cobertura
6. **Quality Gate** - Verifica 99% de cobertura
7. **Package** - Empacota JAR (apenas se Quality Gate passar)

### Pipeline TEST-DEV (`Jenkinsfile.test-dev`)

Similar ao DEV, focado em testes e análises.

### Pipeline IMAGE-DOCKER (`Jenkinsfile.image-docker`)

1. **Checkout** - Clona repositório
2. **Build JAR** - Compila e empacota
3. **Build Docker Image** - Constrói imagem Docker
4. **Push Docker Image** - Publica no Docker Hub

### Pipeline STAGING (`Jenkinsfile.staging`)

1. **Checkout** - Clona repositório
2. **Start container** - Baixa imagem do Docker Hub e inicia
3. **Run tests** - Verifica health check

### Pipeline PROD (`Jenkinsfile.prod`)

1. **Checkout** - Clona repositório
2. **Start container** - Baixa imagem do Docker Hub e inicia
3. **Run tests** - Verifica health check

---

## 🎯 Fluxo Completo de CI/CD

### Fluxo Recomendado:

1. **Desenvolvedor faz commit** → Push para repositório Git
2. **Pipeline DEV** executa automaticamente (ou manualmente):
   - Build
   - Testes
   - Análise de código
   - Quality Gate
3. **Se Quality Gate passar** → Pipeline IMAGE-DOCKER executa:
   - Build da imagem Docker
   - Push para Docker Hub
4. **Pipeline STAGING** executa (manual ou automático):
   - Deploy em ambiente de staging
   - Testes de integração
5. **Pipeline PROD** executa (manual, após aprovação):
   - Deploy em produção

### Configurando Webhooks (Opcional)

Para executar pipelines automaticamente ao fazer push:

#### Para GitHub:

1. No repositório GitHub, vá em **Settings** > **Webhooks** > **Add webhook**
2. Configure:
   - **Payload URL**: `http://seu-ip-ou-dominio:8080/github-webhook/`
   - **Content type**: `application/json`
   - **Events**: Selecione **"Just the push event"**
   - **Active**: Marque a opção
3. Clique em **"Add webhook"**
4. No Jenkins, no pipeline, configure:
   - Vá em **"Configure"** do pipeline
   - Na seção **"Build Triggers"**
   - Marque **"GitHub hook trigger for GITScm polling"**
   - Salve

#### Para GitLab:

1. No repositório GitLab, vá em **Settings** > **Webhooks**
2. Configure:
   - **URL**: `http://seu-ip-ou-dominio:8080/project/seu-pipeline`
   - **Trigger**: Marque **"Push events"**
   - **SSL verification**: Desmarque se usar HTTP (não recomendado para produção)
3. Clique em **"Add webhook"**
4. Teste o webhook clicando em **"Test"** > **"Push events"**

#### Para Bitbucket:

1. No repositório Bitbucket, vá em **Settings** > **Webhooks**
2. Adicione webhook:
   - **Title**: `Jenkins CI/CD`
   - **URL**: `http://seu-ip-ou-dominio:8080/bitbucket-hook/`
   - **Triggers**: Marque **"Repository push"**
3. Salve

**Nota**: Se o Jenkins estiver rodando localmente, você precisará:
- Usar um serviço como **ngrok** para expor o Jenkins na internet
- Ou configurar port forwarding na sua rede
- Ou usar um servidor com IP público

**Exemplo com ngrok**:
```bash
# Instalar ngrok
# Linux: https://ngrok.com/download
# Ou via snap: snap install ngrok

# Expor Jenkins
ngrok http 8080

# Use a URL fornecida pelo ngrok no webhook
```

---

## 📝 Checklist Final

Antes de considerar tudo configurado, verifique:

### Pré-requisitos
- [ ] Docker instalado e funcionando
- [ ] Docker Compose instalado
- [ ] Git instalado
- [ ] Portas disponíveis (8080, 8585, 8686)

### Docker Hub
- [ ] Conta Docker Hub criada e verificada
- [ ] Username: `rickelmedias` (ou seu username)
- [ ] Login testado: `docker login`
- [ ] Repositório criado (ou será criado no primeiro push)

### Jenkins
- [ ] Jenkins rodando em `http://localhost:8080`
- [ ] Senha inicial obtida e configurada
- [ ] Usuário administrador criado
- [ ] Plugins instalados:
  - [ ] Pipeline
  - [ ] Docker Pipeline
  - [ ] Docker
  - [ ] Git
  - [ ] Maven Integration
  - [ ] JaCoCo
  - [ ] Warnings Next Generation
  - [ ] JUnit
  - [ ] HTML Publisher
  - [ ] Credentials Binding
- [ ] JDK-17 configurado (Name: `JDK-17`)
- [ ] Maven-3.9 configurado (Name: `Maven-3.9`)
- [ ] Credenciais Docker Hub configuradas (ID: `docker-hub-credentials`)

### Pipelines
- [ ] Pipeline DEV criado (`subscription-service-dev`)
- [ ] Pipeline TEST-DEV criado (`subscription-service-test-dev`)
- [ ] Pipeline IMAGE-DOCKER criado (`subscription-service-image-docker`)
- [ ] Pipeline STAGING criado (`subscription-service-staging`)
- [ ] Pipeline PROD criado (`subscription-service-prod`)

### Testes
- [ ] Pipeline DEV executado com sucesso
- [ ] Testes unitários passando
- [ ] Análise PMD funcionando
- [ ] Relatório JaCoCo gerado
- [ ] Quality Gate passando (99% cobertura)
- [ ] JAR gerado corretamente
- [ ] Pipeline IMAGE-DOCKER executado com sucesso
- [ ] Imagem Docker buildada
- [ ] Imagem publicada no Docker Hub (`rickelmedias/subscription-service:latest`)
- [ ] Pipeline STAGING executado com sucesso
- [ ] Container de staging rodando na porta 8686
- [ ] Health check de staging funcionando
- [ ] Pipeline PROD executado com sucesso
- [ ] Container de produção rodando na porta 8585
- [ ] Health check de produção funcionando

### Verificação Final
- [ ] Todos os relatórios aparecem no Jenkins
- [ ] Artifacts podem ser baixados
- [ ] Logs estão legíveis e sem erros
- [ ] Webhooks configurados (opcional)

---

## 🎉 Pronto!

Se você chegou até aqui e todos os pipelines estão funcionando, parabéns! 🎊

Você tem um ambiente Jenkins completo com:
- ✅ Build automatizado
- ✅ Testes automatizados
- ✅ Análise de código
- ✅ Cobertura de código
- ✅ Quality Gate
- ✅ Build e push de imagens Docker
- ✅ Deploy em staging e produção

### Próximos Passos

1. **Configurar Webhooks** para execução automática
2. **Adicionar notificações** (email, Slack, etc.)
3. **Configurar backup** do Jenkins
4. **Adicionar mais testes** para aumentar cobertura
5. **Melhorar análises** de código (SonarQube, etc.)

---

## 📚 Referências

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Docker Hub](https://hub.docker.com/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [PMD Documentation](https://pmd.github.io/)

---

## 🆘 Suporte

Se encontrar problemas não listados neste guia:

1. Verifique os logs do Jenkins: `docker logs jenkins`
2. Verifique os logs do build no Console Output
3. Consulte a documentação oficial do Jenkins
4. Verifique se todas as dependências estão instaladas

---

## 📚 Comandos Úteis

### Jenkins

```bash
# Ver logs do Jenkins
docker logs jenkins

# Ver logs em tempo real
docker logs -f jenkins

# Reiniciar Jenkins
docker restart jenkins

# Parar Jenkins
docker stop jenkins

# Iniciar Jenkins
docker start jenkins

# Remover Jenkins (cuidado: perde dados se não tiver backup)
docker stop jenkins
docker rm jenkins
```

### Docker

```bash
# Listar imagens
docker images

# Listar containers
docker ps -a

# Remover imagens antigas
docker image prune -a

# Limpar sistema Docker
docker system prune -a

# Ver uso de disco
docker system df
```

### Docker Compose

```bash
# Iniciar staging
docker-compose -f docker-compose.staging.yml up -d

# Parar staging
docker-compose -f docker-compose.staging.yml down

# Ver logs staging
docker-compose -f docker-compose.staging.yml logs -f

# Iniciar produção
docker-compose -f docker-compose.prod.yml up -d

# Parar produção
docker-compose -f docker-compose.prod.yml down
```

### Maven

```bash
# Limpar e compilar
mvn clean compile

# Executar testes
mvn test

# Gerar relatório de cobertura
mvn jacoco:report

# Verificar qualidade
mvn jacoco:check

# Análise PMD
mvn pmd:pmd

# Empacotar
mvn package

# Instalar no repositório local
mvn install
```

### Git

```bash
# Clonar repositório
git clone <url-do-repositorio>

# Fazer commit e push
git add .
git commit -m "Mensagem do commit"
git push origin main
```

---

## 🚀 Resumo Rápido

### Instalação Rápida do Jenkins

```bash
# 1. Criar diretório
mkdir -p ~/jenkins_home
sudo chown -R 1000:1000 ~/jenkins_home

# 2. Executar Jenkins
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  --group-add $(stat -c %g /var/run/docker.sock) \
  --restart unless-stopped \
  jenkins/jenkins:lts

# 3. Obter senha inicial
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# 4. Acessar
# http://localhost:8080
```

### Configuração Rápida

1. **Acesse**: `http://localhost:8080`
2. **Cole a senha inicial**
3. **Instale plugins sugeridos**
4. **Crie usuário administrador**
5. **Configure ferramentas**:
   - JDK-17 (instalação automática)
   - Maven-3.9 (instalação automática)
6. **Configure credenciais**:
   - ID: `docker-hub-credentials`
   - Username: `rickelmedias`
   - Password: sua senha do Docker Hub
7. **Crie pipelines** usando os Jenkinsfiles do repositório

### Ordem de Execução dos Pipelines

1. **DEV** → Build, testes, qualidade
2. **IMAGE-DOCKER** → Build e push da imagem (após DEV passar)
3. **STAGING** → Deploy em staging (após IMAGE-DOCKER)
4. **PROD** → Deploy em produção (manual, após aprovação)

---

## 📖 Glossário

- **CI/CD**: Continuous Integration / Continuous Deployment
- **Pipeline**: Sequência automatizada de etapas de build e deploy
- **Stage**: Etapa individual dentro de um pipeline
- **Agent**: Nó que executa os builds (pode ser o próprio Jenkins)
- **Artifact**: Arquivo gerado pelo build (ex: JAR)
- **Quality Gate**: Verificação de qualidade (ex: cobertura de código)
- **Webhook**: Notificação automática quando há mudanças no repositório
- **Docker Hub**: Repositório de imagens Docker
- **JaCoCo**: Ferramenta de análise de cobertura de código
- **PMD**: Ferramenta de análise estática de código
- **Maven**: Ferramenta de build e gerenciamento de dependências

---

**Última atualização**: 2025-11-08
**Versão**: 1.0
**Autor**: Adaptado para subscription-service
**Projeto**: subscription-service
**Docker Hub**: rickelmedias/subscription-service

