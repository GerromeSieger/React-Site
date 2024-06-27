pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = credentials('docker-image')
        HOST_IP = credentials('host-ip')
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-cred')
    }
    stages {
        stage('Build and Push Docker Image') {
            steps {
                script {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                    def customImage = docker.build("${DOCKER_IMAGE}")
                    customImage.push()
                    customImage.push('latest')
                    sh 'docker logout'
                }
            }
        }

        stage('Test') {
            environment {
                SONAR_TOKEN = credentials('SONAR_TOKEN')
                SONAR_HOST_URL = credentials('SONAR_HOST_URL')
                PROJECT_KEY = credentials('PROJECT_KEY')
            } 
            agent {
                docker { image 'sonarsource/sonar-scanner-cli:latest' }  
            }
            steps {
                script {
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

        stage ('Deploy') {
            agent { 
                docker { image 'ubuntu:22.04' }  
            }      
            steps {
                unstash 'build-artifact'
                withCredentials([sshUserPrivateKey(credentialsId: 'remote-server-cred', keyFileVariable: 'SSH_PRIVATE_KEY', usernameVariable: 'REMOTE_USER')]) {
                    sh """
                    apt update && apt install openssh-client -y
                    ssh -o StrictHostKeyChecking=no -i ${SSH_PRIVATE_KEY} ${REMOTE_USER}@${HOST_IP} '
                    docker pull gerrome/react-site
                    docker run -p 5000:80 -d gerrome/react-site 
                    '
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}