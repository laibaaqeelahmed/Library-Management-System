pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                echo 'Building Library Management System...'

                bat '''
                    if not exist bin mkdir bin
                    javac -d bin src\\main\\java\\*.java
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running Library Management System Tests...'

                bat 'java -cp bin Main'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Library Management System...'

                bat '''
                    if not exist deployment mkdir deployment
                    xcopy bin deployment /E /I /Y
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Please check the errors.'
        }
    }
}