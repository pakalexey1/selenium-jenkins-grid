pipeline {
  agent any

  options {
    timestamps()
  }

  tools {
    maven 'Maven3'
    jdk   'Java17'
  }

  environment {
    GRID_URL = 'http://54.90.94.39:4444/wd/hub'
  }

  stages {
    stage('Build & Test') {
      steps {
        sh """
          mvn -q \
            -Dheadless=true \
            -DrunTarget=grid \
            -DgridUrl=${GRID_URL} \
            -Djava.awt.headless=true \
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
