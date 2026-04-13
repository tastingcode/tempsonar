pipeline {
    agent any

    parameters {
        string(name: 'SOURCE_DIR', defaultValue: '/workspace/tempsonar', description: 'Jenkins 컨테이너 안에서 보이는 프로젝트 경로')
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
                dir(params.SOURCE_DIR) {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew --version'
                }
            }
        }

        stage('Test') {
            steps {
                dir(params.SOURCE_DIR) {
                    sh './gradlew clean test jacocoTestReport'
                }
            }
            post {
                always {
                    dir(params.SOURCE_DIR) {
                        junit 'build/test-results/test/*.xml'
                        archiveArtifacts artifacts: 'build/reports/jacoco/test/html/**', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Build') {
            steps {
                dir(params.SOURCE_DIR) {
                    sh './gradlew bootJar'
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir(params.SOURCE_DIR) {
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
}
