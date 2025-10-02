pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
  }

  tools {
    maven 'Maven3'
    jdk   'Java17'
  }

  stages {
    stage('Build & Test') {
      steps {
        sh '''
          mvn -q \
            -Dheadless=true \
            -DrunTarget=grid \
            -DgridUrl=http://54.90.94.39:4444/wd/hub \
            -Djava.awt.headless=true \
            clean test
        '''
      }
    }
  }

  post {
    always {
      junit 'target/surefire-reports/*.xml'
    }
  }
}
