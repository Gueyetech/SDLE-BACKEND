pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'yadex34'
        IMAGE_NAME = 'sdle-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKER_HUB_USER}/${IMAGE_NAME}"
    }

    tools {
        maven 'Maven-3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Compilation du projet Spring Boot...'
                sh 'mvn clean compile -B'
            }
        }

        stage('Tests') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                sh 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Security Scan - Code Source') {
            steps {
                echo '🔒 Analyse de sécurité du code source avec Trivy...'
                sh '''
                    # Scan du filesystem pour détecter les vulnérabilités dans les dépendances
                    trivy fs --severity HIGH,CRITICAL --format table . || true
                '''
            }
        }

        stage('Package') {
            steps {
                echo '📦 Packaging du JAR...'
                sh 'mvn package -DskipTests -B'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Security Scan - Docker Image') {
            steps {
                echo '🔒 Scan de vulnérabilités de l\'image Docker avec Trivy...'
                sh """
                    trivy image --severity HIGH,CRITICAL --format table ${DOCKER_IMAGE}:${IMAGE_TAG} || true
                """
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo '🚀 Push de l\'image vers Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline CI Backend terminé avec succès !'
        }
        failure {
            echo '❌ Pipeline CI Backend échoué.'
        }
        always {
            echo '🧹 Nettoyage...'
            sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} || true"
        }
    }
}
