pipeline {
    agent any

    stages {
        agent {
            docker { image 'sonarsource/sonar-scanner-cli:latest' }  
          }
        stage('SonarQube Analysis') {
            environment {
                SONAR_TOKEN = credentials('SONAR_TOKEN')
                SONAR_HOST_URL = credentials('SONAR_HOST_URL')
                PROJECT_KEY = credentials('PROJECT_KEY')
            }
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh """
                            sonar-scanner \
                            -Dsonar.projectKey=${PROJECT_KEY} \
                            -Dsonar.sources=. \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }
    }
}
