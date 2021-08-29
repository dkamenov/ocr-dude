pipeline {
    agent any
    stages {
        stage('build') {
            parallel {
                stage('Mac OS Build') {
                    agent { label 'OS_X' }
                    steps {
                        script {
                            copyArtifacts(projectName: 'Tesseract-libs', filter: "tesseract/build/.libs/libtesseract.5.dylib",
                                    target: 'src/main/resources/lib', selector: lastSuccessful(), flatten: true);
                        }
                        sh './gradlew clean installer'
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'build/installer/*'
                        }
                    }
                }

                stage('Ubuntu Build') {
                    agent { label 'linux' }
                    steps {
                        script {
                            copyArtifacts(projectName: 'Tesseract-libs', filter: "tesseract/build/.libs/libtesseract.5.dylib",
                                    target: 'src/main/resources/lib', selector: lastSuccessful(), flatten: true);
                        }
                        sh './gradlew clean installer'
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'build/installer/*'
                        }
                    }
                }

                stage('Windows Build') {
                    agent { label 'win' }
                    steps {
                        script {
                            copyArtifacts(projectName: 'Tesseract-libs', filter: "tesseract/build/.libs/libtesseract.5.dylib",
                                    target: 'src/main/resources/lib', selector: lastSuccessful(), flatten: true);
                        }
                        sh 'gradlew.bat clean installer'
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