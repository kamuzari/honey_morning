pipeline {
    agent any

    stages {
        stage('코드 체크아웃') {
            steps {
                // GitLab 저장소의 develop 브랜치에서 코드 체크아웃
                git branch: 'develop',
                    url: 'https://lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git',
                    credentialsId: 'wngud1225'  // Jenkins에 등록된 Username/Password 자격 증명 ID
            }
        }

        stage('백엔드 빌드') {
            steps {
                dir('BE') {
                    // 백엔드 Docker 이미지 빌드
                    script {
                        docker.build('hm-backend:latest', '-f Dockerfile .')
                    }
                }
            }
        }

        stage('프론트엔드 빌드') {
            steps {
                dir('FE/honey-morning') {
                    // 프론트엔드 Docker 이미지 빌드
                    script {
                        docker.build('hm-frontend:latest', '-f Dockerfile .')
                    }
                }
            }
        }

        stage('기존 컨테이너 중지 및 제거') {
            steps {
                script {
                    // 기존 백엔드 및 프론트엔드 컨테이너 중지 및 제거
                    sh '''
                    docker stop hm-backend || true && docker rm hm-backend || true
                    docker stop hm-frontend || true && docker rm hm-frontend || true
                    '''
                }
            }
        }

        stage('백엔드 컨테이너 실행') {
            steps {
                script {
                    // 새로운 백엔드 컨테이너 실행
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

        stage('프론트엔드 컨테이너 실행') {
            steps {
                script {
                    // 새로운 프론트엔드 컨테이너 실행
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
            echo '빌드 성공!'
        }
        failure {
            echo '빌드 실패!'
        }
    }
}
