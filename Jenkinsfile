pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'Java17'
    }

    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn -q -Dselenium.grid.url=http://54.90.94.39:4444 -Dheadless=true clean test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
