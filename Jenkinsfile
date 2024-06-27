pipeline {
    agent any
    
    environment {
        NODE_VERSION = '18.0.0'
        IP_CRED = credentials('host-ip') 
    }
    stages {
        stage('Build') {
            agent { 
                docker { image 'node:18-alpine' }  
              }                
            steps {
                sh 'npm install'
                sh 'npm run build'
                stash includes: 'build/**', name: 'build-artifact'
            }
        }

        stage('Test') {
            steps {
                unstash 'build-artifact'
                sh 'chmod +x ./test.sh'
                sh './test.sh'
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
                scp -o StrictHostKeyChecking=no -i ${SSH_PRIVATE_KEY} -r ./build ${REMOTE_USER}@${IP_CRED}:/root
                ssh -o StrictHostKeyChecking=no -i ${SSH_PRIVATE_KEY} ${REMOTE_USER}@${IP_CRED} '
                sudo cp -r build/* /var/www/html
                sudo systemctl restart nginx
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