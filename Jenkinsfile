pipeline {
    agent any
    
    environment {
        NODE_VERSION = '18'
        IP_CRED = credentials('host-ip') 
    }
    stages {
        stage('Build') {
            steps {
                nodejs(nodeJSInstallationName: "NodeJS ${NODE_VERSION}") {
                    sh 'npm install'
                    sh 'npm run build'
                }
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
                ssh -o StrictHostKeyChecking=no -i ${SSH_PRIVATE_KEY} ${REMOTE_USER}@${IP_CRED} '
                sudo apt update
                pwd
                ls -al
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