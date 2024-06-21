pipeline {
  agent any
  stages {
    stage('Build') { 
      agent {
        docker { image 'node:18-alpine' }  
      }        
      steps {
        sh 'yarn install' 
        sh 'yarn build' 
      }
    }

    stage('Test') {
      steps {
        sh 'echo yarn install'
        sh 'echo yarn test'
      }
    }

    stage('Deploy') {
      steps {
        sh 'echo ansible-playbook playbooks/deploy.yml'  
      }
    }
  }
}