pipeline {
    agent any

    environment {
        APP_PROPS = credentials('application-properties')
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'develop',
                    url: 'https://lab.ssafy.com/s11-ai-speech-sub1/S11P21A704.git',
                    credentialsId: 'wngud1225'
            }
        }

        stage('Prepare Application Properties') {
            steps {
                sh 'mkdir -p BE/src/main/resources'
                sh 'cp $APP_PROPS BE/src/main/resources/application.properties'
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
                    sh 'docker build -t frontend:latest -f Dockerfile .'
                }
            }
        }

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

        stage('Run Backend Container') {
            steps {
                sh '''
                    docker run -d \
                    --name hm-backend \
                    -p 8081:8081 \
                    --network hm-network \
                    -e SPRING_DATASOURCE_URL="jdbc:mysql://hm-mysql:3306/honeymorning?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
                    -e SPRING_DATASOURCE_USERNAME=ssafy \
                    -e SPRING_DATASOURCE_PASSWORD=ssafy \
                    -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                    -e SPRING_DATA_REDIS_HOST=hm-redis \
                    -e SPRING_DATA_REDIS_PORT=6379 \
                    -v $APP_PROPS:/app/application.properties \
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
                    --link hm-backend:hm-backend \
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
            echo '빌드 성공!'
        }
        failure {
            echo '빌드 실패!'
        }
    }
}