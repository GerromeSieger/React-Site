pipeline {
  agent {
    docker {
      label 'docker1'
      image 'jenkins-docker-agent'  // This should match the name of the image you built
    }
  }       
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