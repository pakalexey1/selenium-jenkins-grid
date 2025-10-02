pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'Java17'
    }

stage('Build & Test') {
    steps {
        sh '''
            mvn -q \
              -DrunTarget=grid \
              -DgridUrl=http://54.90.94.39:4444 \
              -Dbrowser=chrome \
              -Dheadless=true \
              clean test
        '''
    }
}

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
