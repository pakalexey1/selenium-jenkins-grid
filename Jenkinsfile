pipeline {
    agent any

    tools {
        maven 'Maven3'       // Make sure "Maven3" is configured in Jenkins global tools
        jdk 'Java17'         // Make sure "Java17" is configured in Jenkins global tools
    }

    stages {
        stage('Build & Test') {
            steps {
                withEnv([
                    "PATH+MAVEN=${tool 'Maven3'}/bin",
                    "JAVA_HOME=${tool 'Java17'}"
                ]) {
                    // Run tests against Selenium Grid
                    sh '''
                        mvn -q \
                          -DrunTarget=grid \
                          -DgridUrl=http://selenium-hub:4444/wd/hub \
                          -Dheadless=true \
                          clean test
                    '''
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
