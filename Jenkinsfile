pipeline {
    agent any

    environment {
        DOCKER_NETWORK = 'hm-network'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the code...'
                // 소스코드를 Jenkins 작업 디렉토리로 체크아웃
                git 'https://lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git'
            }
        }

        stage('Build Backend') {
            steps {
                echo 'Building backend...'
                dir('BE') {
                    script {
                        sh 'docker build -t hm-backend .'
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Building frontend...'
                dir('FE/honey-morning') {
                    script {
                        sh 'docker build -t hm-frontend .'
                    }
                }
            }
        }

        stage('Start Services with Docker Compose') {
            steps {
                echo 'Starting services with docker-compose...'
                // 기존의 MySQL 및 Redis 컨테이너가 이미 실행 중인 상태라고 가정
                sh 'docker-compose up -d nginx backend frontend certbot'
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Verifying deployment...'
                // Nginx가 80번 포트에서 작동하는지 확인
                sh 'curl -I http://localhost'
            }
        }

    }

    post {
        always {
            echo 'Cleaning up...'
            // 배포 후 필요하면 컨테이너를 정리할 수 있음
            sh 'docker-compose down'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
