pipeline {
  agent any
  triggers {
    githubPush()
  }
  stages {
    stage('Build') { 
      steps {
        sh 'echo yarn install' 
        sh 'echo yarn build' 
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