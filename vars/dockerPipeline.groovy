def call(Map config = [:]) {
    pipeline {
        agent any

        environment {
            IMAGE_NAME = config.imageName ?: "ray/image"
            TAG = config.tag ?: "latest"
        }

        stages {
            stage('Clone Repo') {
                steps {
                    git url: config.repoUrl, branch: config.branch ?: 'main'
                }
            }

            stage('Build Docker Image') {
                steps {
                    sh "docker build -t ${IMAGE_NAME}:${TAG} ."
                }
            }

            stage('Push Docker Image') {
                steps {
                    withDockerRegistry([credentialsId: config.credentialsId, url: ""]) {
                        sh "docker push ${IMAGE_NAME}:${TAG}"
                    }
                }
            }
        }
    }
}
