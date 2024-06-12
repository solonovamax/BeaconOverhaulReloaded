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
        //         buildingTag()
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
            cleanWs()
        }
    }
}
