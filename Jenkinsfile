pipeline {
  agent any

  tools {
    maven 'Maven3'     // Jenkins > Manage Jenkins > Tools names
    jdk   'Java17'
  }

  options {
    timestamps()
    ansiColor('xterm')
  }

  environment {
    GRID_URL = 'http://54.90.94.39:4444'   // your Selenium Grid hub
  }

  stages {
    stage('Build & Test') {
      steps {
        sh """
          mvn -q \
            -Dselenium.grid.url=${GRID_URL} \
            -Dheadless=true \
            clean test
        """
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }
}