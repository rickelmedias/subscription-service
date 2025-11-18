pipeline {
    agent any
    
    tools {
        // Assume que estas ferramentas estão configuradas
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    // VARIÁVEIS DE AMBIENTE
    environment {
        DOCKER_IMAGE = 'rickelmedias/subscription-service'
        DOCKER_TAG = "${BUILD_NUMBER}"
        // A tag 0.99 é mais clara aqui, mas o Jacoco check usa o pom.xml
        QUALITY_GATE_THRESHOLD = '0.99' 
    }
    
    stages {
        stage('Checkout SCM') {
            steps {
                echo '🔄 Clonando repositório e verificando a branch...'
                checkout scm
            }
        }
        
        stage('Build & Test') {
            steps {
                echo '🔨 Compilando, executando Testes Unitários e gerando relatórios JaCoCo...'
                // Usa 'test' para garantir compilação, testes e geração do jacoco.exec.
                sh 'mvn clean install'
            }
            post {
                // Publicação dos relatórios JUnit
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Analysis (PMD)') {
            steps {
                echo '🔍 Executando Análise Estática de Código (PMD)...'
                // O pmd:check é mais rigoroso e falha o build em caso de violações
                sh 'mvn pmd:pmd pmd:check' 
            }
            post {
                // Publicação do relatório PMD
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tools: [pmdParser(pattern: '**/target/pmd.xml')]
                    )
                }
            }
        }
        
        stage('Coverage & Quality Gate') {
            steps {
                echo "📊 Verificando Cobertura de Código e aplicando Quality Gate (Min: ${env.QUALITY_GATE_THRESHOLD})..."
                
                // O jacoco:check usa as regras configuradas no pom.xml e falha o pipeline se a cobertura for baixa.
                sh 'mvn jacoco:report jacoco:check'
                
                // Se o comando acima for bem-sucedido, o gate PASSOU.
                echo "✅ Quality Gate PASSOU! Cobertura mínima atingida."
            }
            post {
                // Publicação do relatório JaCoCo
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        inclusionPattern: '**/*.class',
                        exclusionPattern: '**/dto/**,**/config/**,**/SubscriptionApplication.class'
                    )
                }
            }
        }
        
        stage('Package Artifact') {
            // Este stage só será executado se todos os stages anteriores (incluindo o Quality Gate) passarem.
            // A condição 'when' original não é mais necessária, mas podemos usá-la para clareza.
            steps {
                echo '📦 Empacotando aplicação (Pulando testes novamente)...'
                sh 'mvn package -DskipTests'
            }
        }
    }
    
    // AÇÕES FINAIS E LIMPEZA
    post {
        always {
            echo '🧹 Limpeza, Arquivamento e Finalização...'
            // Arquivamento consolidado: JAR, relatórios Surefire, PMD e JaCoCo
            archiveArtifacts artifacts: '**/target/*.jar, **/target/surefire-reports/*, **/target/pmd.xml, **/target/site/jacoco/**', fingerprint: true
        }
        success {
            echo '✅ Pipeline DEV executado com SUCESSO! Artefatos prontos.'
        }
        failure {
            echo '❌ Pipeline DEV FALHOU! Verifique o log para detalhes sobre falhas de Teste/PMD/Cobertura.'
        }
    }
}
