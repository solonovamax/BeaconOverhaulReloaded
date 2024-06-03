pipeline {
    agent any

    tools {
        jdk "Temurin Java 17"
    }

    environment {

    }

    stages {
        stage('Clean') {
            steps {
                sh 'chmod +x gradlew'
                withGradle {
                    sh './gradlew clean'
                }
            }
        }
        stage('Build') {
            steps {
                echo 'Remapping classpath'
                withGradle {
                    sh './gradlew generateRemapClasspath'
                }
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
        // stage('Deploy Release') {
        //     when {
        //         tag 'v*'
        //     }
        //     steps {
        //         withGradle {
        //             sh './gradlew publish'
        //         }
        //     }
        // }
        // stage('Publish Snapshot') {
        //     when {
        //         not {
        //             tag 'v*'
        //         }
        //     }
        //     steps {
        //         withGradle {
        //             sh './gradlew publish'
        //         }
        //     }
        // }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
        }
    }
}
