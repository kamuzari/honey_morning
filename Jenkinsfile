pipeline {
    agent any

    environment {
        APP_PROPS = credentials('application-properties')
        FRONTEND_ENV = credentials('frontend-env')
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

        stage('Prepare Frontend ENV') {
            steps {
                dir('FE/honey-morning') {
                    writeFile file: '.env', text: env.FRONTEND_ENV
                    sh 'cat .env'
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
                        sh "docker build -t frontend:latest --build-arg VITE_BASE_URL=${viteBaseUrl} -f Dockerfile ."
                    }
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
                    -v $APP_PROPS:/app/config/application.properties \
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