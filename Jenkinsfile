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
        sh 'echo pip install -r requirements.txt'
        sh 'echo pytest tests/'
      }
    }

    stage('Deploy') {
      steps {
        sh 'echo ansible-playbook playbooks/deploy.yml'  
      }
    }
  }
}