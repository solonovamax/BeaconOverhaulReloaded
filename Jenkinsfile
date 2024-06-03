pipeline {
    agent any

    tools {
        jdk "Temurin Java 17"
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
    // post {
    //     always {
    //         archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
    //     }
    // }
}
