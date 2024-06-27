pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = credentials('docker-image')
        HOST_IP = credentials('host-ip')
    }
    stages {

        stage('Build') {
            agent { 
                docker { image 'docker:latest' }  
              }                
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
                withCredentials([
                    usernamePassword(credentials: 'dockerhub-cred', usernameVariable: DOCKERHUB_USERNAME, passwordVariable: DOCKERHUB_PASSWORD)
                ])
                sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD} && docker push ${DOCKER_IMAGE}"                 
            }   
        }

        stage ('Deploy') {
            agent { 
                docker { image 'ubuntu:22.04' }  
              }      
            steps{
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