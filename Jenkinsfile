pipeline {
    agent any

    tools {
        jdk "Temurin Java 21"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Setup Gradle') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Remapping Classpath') {
            steps {
                withGradle {
                    sh './gradlew generateRemapClasspath'
                }
            }
        }
        stage('Clean') {
            steps {
                withGradle {
                    sh './gradlew clean'
                }
            }
        }
        stage('Build') {
            steps {
                withGradle {
                    sh './gradlew build'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true, onlyIfSuccessful: true
                }
            }
        }
        // TODO: publish to modrinth automatically on release
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
    }
}
