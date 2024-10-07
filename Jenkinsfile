pipeline {
    agent any

    environment {
        APP_PROPS = credentials('application-properties')
        FRONTEND_ENV = credentials('frontend-env')
    }

    stages {
        stage('Prepare Git Info') {
            steps {
                script {
                    env.GIT_AUTHOR_NAME = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    env.GIT_AUTHOR_EMAIL = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                }
            }
        }

        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM', 
                    branches: [[name: '*/develop']], 
                    userRemoteConfigs: [[
                        url: 'https://lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git',
                        credentialsId: 'wngud1225'
                    ]]
                ])
            }
        }

        stage('Prepare Application Properties') {
            steps {
                sh 'mkdir -p BE/src/main/resources'
                sh 'cp $APP_PROPS BE/src/main/resources/application.properties'
            }
        }

        stage('Prepare Frontend ENV') {
            steps {
                dir('FE/honey-morning') {
                    writeFile file: '.env', text: env.FRONTEND_ENV
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('BE') {
                    sh 'docker build -t backend:latest -f Dockerfile .'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('FE/honey-morning') {
                    script {
                        def viteBaseUrl = sh(script: "grep VITE_BASE_URL $FRONTEND_ENV | cut -d '=' -f2", returnStdout: true).trim()
                        def viteProjectDataUrl = sh(script: "grep VITE_PROJECT_DATA_URL $FRONTEND_ENV | cut -d '=' -f2", returnStdout: true).trim()
                        
                        echo "Building frontend with VITE_BASE_URL: ${viteBaseUrl} and VITE_PROJECT_DATA_URL: ${viteProjectDataUrl}"
                        
                        sh """
                            docker build -t frontend:latest \
                            --build-arg VITE_BASE_URL=${viteBaseUrl} \
                            --build-arg VITE_PROJECT_DATA_URL=${viteProjectDataUrl} \
                            -f Dockerfile .
                        """
                    }
                }
            }
        }

        stage('Stop and Remove Existing Containers') {
            steps {
                script {
                    def containerNames = ['hm-backend', 'hm-frontend']
                    
                    containerNames.each { containerName ->
                        sh "docker stop ${containerName} || true"
                        sh "docker rm ${containerName} || true"
                        
                        def containerExists = sh(script: "docker ps -a --format '{{.Names}}' | grep -q '^${containerName}\$'", returnStatus: true) == 0
                        
                        if (containerExists) {
                            echo "Container ${containerName} still exists. Attempting force removal..."
                            sh "docker rm -f ${containerName} || true"
                            
                            containerExists = sh(script: "docker ps -a --format '{{.Names}}' | grep -q '^${containerName}\$'", returnStatus: true) == 0
                            
                            if (containerExists) {
                                error "Failed to remove ${containerName} container even after force removal"
                            }
                        }
                        
                        echo "Container ${containerName} successfully removed"
                    }
                }
            }
        }

        stage('Run Backend Container') {
            steps {
                sh '''
                    docker run -d \
                    --name hm-backend \
                    -p 8081:8081 \
                    --network hm-network \
                    -v $APP_PROPS:/app/config/application.properties \
                    -v /home/ubuntu/project_data:/app/project_data \
                    -e TZ=Asia/Seoul \
                    backend:latest
                '''
            }
        }

        stage('Run Frontend Container') {
            steps {
                sh '''
                docker run -d \
                    --name hm-frontend \
                    -p 5173:5173 \
                    --network hm-network \
                    -e VITE_BASE_URL=$(grep VITE_BASE_URL $FRONTEND_ENV | cut -d '=' -f2) \
                    -e TZ=Asia/Seoul \
                    frontend:latest
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            script {
                mattermostSend(
                    color: 'good',
                    message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${env.GIT_AUTHOR_NAME}(${env.GIT_AUTHOR_EMAIL})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/pw558un543fx9g8nmrc1o8rqyr',
                    channel: 'A704-PR'
                )
            }
        }
        failure {
            script {
                mattermostSend(
                    color: 'danger',
                    message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${env.GIT_AUTHOR_NAME}(${env.GIT_AUTHOR_EMAIL})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/pw558un543fx9g8nmrc1o8rqyr',
                    channel: 'A704-PR'
                )
            }
        }
    }
}