def LABEL_LINUX='linux'
def LABEL_WINDOWS='win'
def LABEL_OSX='OS_X'

pipeline {
    agent any
    stages {
        stage('build') {
            parallel {
                stage('Mac OS Build') {
                    agent { label LABEL_OSX }
                    when {
                        beforeAgent true
                        expression {
                            return nodesByLabel(LABEL_OSX).size() > 0
                        }
                    }
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
                    agent { label LABEL_LINUX }
                    when {
                        beforeAgent true
                        expression {
                            return nodesByLabel(LABEL_LINUX).size() > 0
                        }
                    }
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
                    agent { label LABEL_WINDOWS }
                    when {
                        beforeAgent true
                        expression {
                            return nodesByLabel(LABEL_WINDOWS).size() > 0
                        }
                    }
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