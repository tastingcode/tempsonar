pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        GRADLE_USER_HOME = "${JENKINS_HOME}/.gradle"
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'tempsonar'
    }

    stages {
        stage('Prepare') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew --version'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew clean test jacocoTestReport'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                    archiveArtifacts artifacts: 'build/reports/jacoco/test/html/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew bootJar'
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        ./gradlew --no-daemon sonar \
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.token=${SONAR_TOKEN} \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY}
                    '''
                }
            }
        }
    }
}
