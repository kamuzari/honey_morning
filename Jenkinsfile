pipeline {
    agent any
    environment {
        GIT_CREDENTIALS = credentials('153d5dcc-2f40-421c-8e4b-552d35dad7e1')
    }

    stages {
        stage('Checkout Code') {
            steps {
                // GitLab
                git branch: 'develop',
                    url: 'https://oauth2:${GIT_CREDENTIALS}@lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git'
            }
        }

        // 백엔드 Docker 이미지 빌드
        stage('Build Backend') {
            steps {
                dir('BE') {

                    script {
                        docker.build('hm-backend:latest', '-f Dockerfile .')
                    }
                }
            }
        }

        // 프론트엔드 Docker 이미지 빌드
        stage('Build Frontend') {
            steps {
                dir('FE/honey-morning') {

                    script {
                        docker.build('hm-frontend:latest', '-f Dockerfile .')
                    }
                }
            }
        }

         // 기존 컨테이너 중지 및 제거 (이미 없으면 에러 무시)
        stage('Stop and Remove Existing Containers') {
            steps {
                script {
                    sh '''
                    docker stop hm-backend || true && docker rm hm-backend || true
                    docker stop hm-frontend || true && docker rm hm-frontend || true
                    '''
                }
            }
        }

        // 새로운 백엔드 컨테이너 실행
        stage('Run Backend Container') {
            steps {
                script {
                    sh '''
                    docker run -d \
                        --name hm-backend \
                        -p 8081:8081 \
                        --network hm-network \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://hm-mysql:3306/honeymorning?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC \
                        -e SPRING_DATASOURCE_USERNAME=ssafy \
                        -e SPRING_DATASOURCE_PASSWORD=ssafy \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        -e SPRING_JWT_REDIS_HOST=hm-redis \
                        -e SPRING_JWT_PORT=6379 \
                        hm-backend:latest
                    '''
                }
            }
        }

        // 새로운 프론트엔드 컨테이너 실행
        stage('Run Frontend Container') {
            steps {
                script {
                    sh '''
                    docker run -d \
                        --name hm-frontend \
                        -p 5173:5173 \
                        --network hm-network \
                        --link hm-backend:hm-backend \
                        hm-frontend:latest
                    '''
                }
            }
        }
    }

    post {
        always {
            // 빌드 후 워크스페이스 정리
            cleanWs()
        }
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
