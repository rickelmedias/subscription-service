pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    environment {
        DOCKER_IMAGE = 'rickelmedias/subscription-service'
        DOCKER_TAG = "${BUILD_NUMBER}"
        QUALITY_GATE_THRESHOLD = 0.99
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonando repositório...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Compilando aplicação...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo '🧪 Executando testes unitários...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: '**/target/surefire-reports/**', fingerprint: true
                }
            }
        }
        
        stage('Code Analysis - PMD') {
            steps {
                echo '🔍 Análise estática de código (PMD)...'
                sh 'mvn pmd:pmd'
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tools: [pmdParser(pattern: '**/target/pmd.xml')]
                    )
                    archiveArtifacts artifacts: '**/target/pmd.xml', fingerprint: true
                }
            }
        }
        
        stage('Code Coverage - JaCoCo') {
            steps {
                echo '📊 Gerando relatório de cobertura...'
                sh 'mvn jacoco:report'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        inclusionPattern: '**/*.class',
                        exclusionPattern: '**/dto/**,**/config/**,**/SubscriptionApplication.class'
                    )
                    archiveArtifacts artifacts: '**/target/site/jacoco/**', fingerprint: true
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    echo '🎯 Verificando Quality Gate (99% de cobertura)...'
                    sh 'mvn jacoco:check'
                    
                    def coveragePassed = true
                    try {
                        sh 'mvn jacoco:check'
                    } catch (Exception e) {
                        coveragePassed = false
                        error "❌ Quality Gate FALHOU! Cobertura abaixo de 99%"
                    }
                    
                    if (coveragePassed) {
                        echo "✅ Quality Gate PASSOU! Cobertura >= 99%"
                        env.QUALITY_GATE_PASSED = 'true'
                    }
                }
            }
        }
        
        stage('Package') {
            when {
                expression { env.QUALITY_GATE_PASSED == 'true' }
            }
            steps {
                echo '📦 Empacotando aplicação...'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        always {
            echo '🧹 Limpando workspace...'
        }
        success {
            echo '✅ Pipeline DEV executado com SUCESSO!'
        }
        failure {
            echo '❌ Pipeline DEV FALHOU!'
        }
    }
}
