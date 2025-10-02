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

  stage('Build & Test') {
    steps {
      sh '''
        mvn -q \
          -DrunTarget=grid \
          -DgridUrl=http://54.90.94.39:4444 \
          -Dbrowser=chrome \
          -Dheadless=true \
          -Dwebdriver.http.factory=jdk \
          clean test
      '''
    }
  }

  post {
    always {
      junit 'target/surefire-reports/*.xml'
    }
  }
}
