pipeline {
    agent any

    environment {
        // Change these variables to match your registry configurations
        DOCKER_REGISTRY            = 'testingacountwork' 
        DOCKER_CREDENTIALS_ID      = 'docker-hub-credentials'
        KUBECONFIG_CREDENTIALS_ID  = 'kube-config'
        
        BACKEND_IMAGE              = "${env.DOCKER_REGISTRY}/bank-backend:${env.BUILD_NUMBER}"
        FRONTEND_IMAGE             = "${env.DOCKER_REGISTRY}/bank-frontend:${env.BUILD_NUMBER}"
        NGINX_IMAGE                = "${env.DOCKER_REGISTRY}/bank-nginx:${env.BUILD_NUMBER}"
    }
tools {
        // Automatically sets PATH for ALL stages in the pipeline
        nodejs 'NodeJS-26' 
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Manoj-Bharathi-S/Banking-System'
            }
        }

        stage('Build Backend') {
            steps {
                dir('bank_backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('bank_frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh "docker build -t ${env.BACKEND_IMAGE} ./bank_backend"
                sh "docker build -t ${env.FRONTEND_IMAGE} ./bank_frontend"
                sh "docker build -t ${env.NGINX_IMAGE} ./nginx"
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        sh "docker push ${env.BACKEND_IMAGE}"
                        sh "docker push ${env.FRONTEND_IMAGE}"
                        sh "docker push ${env.NGINX_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Inject kubeconfig configuration from Jenkins Credentials
                    withKubeConfig([credentialsId: env.KUBECONFIG_CREDENTIALS_ID]) {
                        // Dynamically update the image tags in the K8s manifests using sed
                        sh "sed -i 's|bank_backend:latest|${env.BACKEND_IMAGE}|g' k8s/backend.yaml"
                        sh "sed -i 's|bank_frontend:latest|${env.FRONTEND_IMAGE}|g' k8s/frontend.yaml"
                        sh "sed -i 's|image: nginx:alpine|image: ${env.NGINX_IMAGE}|g' k8s/nginx-loadbalancer.yaml"
                        
                        // Apply deployments to the cluster
                        sh "kubectl apply -f k8s/"
                        
                        // Wait for rollouts to finish successfully
                        sh "kubectl rollout status deployment/backend-deployment"
                        sh "kubectl rollout status deployment/frontend-deployment"
                        sh "kubectl rollout status deployment/nginx-loadbalancer-deployment"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            // Remove local build images from agent to save disk space
            sh "docker rmi ${env.BACKEND_IMAGE} ${env.FRONTEND_IMAGE} ${env.NGINX_IMAGE} || true"
        }
    }
}
