pipeline {
    options {
        copyArtifactPermission '*' // allow other jobs to use archived artifacts
    }
    agent any
    stages {
        stage('build') {
            parallel {
                stage('Mac OS Build') {
                    agent { label 'OS_X' }
                    steps {
                        script {
                            copyArtifacts projectName: 'Tesseract-libs', filter: "libtesseract.5.dylib",
                                    target: 'src/main/resources/lib', selector: lastSuccessful()
                        }
                        cleanWs()
                        sh './gradlew installer'
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'build/installer/*'
                        }
                    }
                }
            }
        }
    }
}