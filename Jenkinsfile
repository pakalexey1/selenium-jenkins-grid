pipeline {
  agent any

  tools {
    maven 'Maven3'
    jdk   'Java17'
  }

  options {
    timestamps()
  }

  environment {
    GRID_URL = 'http://54.90.94.39:4444'
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
    }
  }

  post {
    always {
      junit 'target/surefire-reports/*.xml'
    }
  }
}
