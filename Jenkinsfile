pipeline {
    agent any
    stages {
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
        stage('Prepare Workspace') {
            steps {
                script {
                    sh 'mkdir -p workspace'
                    dir('workspace') {
                        sh 'pwd'
                        sh 'ls -la'
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                script {
                    dir('workspace') {
                        try {
                            checkout([
                                $class: 'GitSCM',
                                branches: [[name: '*/develop']],
                                userRemoteConfigs: [[url: 'https://lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git', 
                                credentialsId: 'gitlab-access-token']] // access-token 수정 필요
                            ])
                        } catch (Exception e) {
                            echo "Git checkout failed: ${e}"
                            sh 'pwd'
                            sh 'ls -la'
                            sh 'git rev-parse --is-inside-work-tree || true'
                            sh 'git config --list || true'
                            sh 'git status || true'
                            error("Git checkout failed")
                        }
                    }
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('workspace/FE/honey-morning') {
                    script {
                        sh 'docker build -t hm-frontend:latest .'
                    }
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('workspace/BE') {
                    script {
                        sh './gradlew build'
                        sh 'docker build -t hm-backend:latest .'
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // 기존 컨테이너 중지 및 제거
                    sh 'docker stop hm-frontend hm-backend || true'
                    sh 'docker rm hm-frontend hm-backend || true'
                    
                    // 네트워크 생성 (이미 존재하는 경우 무시)
                    sh 'docker network create hm-network || true'
                    
                    // 백엔드 배포
                    sh '''
                    docker run -d --name hm-backend \
                        --network hm-network \
                        -p 8081:8081 \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://hm-mysql:3306/honeymorning?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC \
                        -e SPRING_DATASOURCE_USERNAME=ssafy \
                        -e SPRING_DATASOURCE_PASSWORD=ssafy \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        -e SPRING_JWT_REDIS_HOST=hm-redis \
                        -e SPRING_JWT_PORT=6379 \
                        hm-backend:latest
                    '''
                    
                    // 프론트엔드 배포
                    sh '''
                    docker run -d --name hm-frontend \
                        --network hm-network \
                        -p 5173:5173 \
                        hm-frontend:latest
                    '''
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}