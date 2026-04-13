pipeline {
    agent any

    parameters {
        string(name: 'SONAR_HOST_URL', defaultValue: 'http://sonarqube:9000', description: 'Docker Compose network에서 Jenkins가 바라보는 SonarQube URL')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'tempsonar', description: 'SonarQube project key')
        password(name: 'SONAR_TOKEN', defaultValue: '', description: 'SonarQube에서 발급한 token')
    }

    environment {
        GRADLE_USER_HOME = "${JENKINS_HOME}/.gradle"
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
                sh '''
                    ./gradlew sonar \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.token=${SONAR_TOKEN} \
                      -Dsonar.projectKey=${SONAR_PROJECT_KEY}
                '''
            }
        }
    }
}
