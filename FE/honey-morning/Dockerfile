# Node.js 20.14.0 버전 사용
FROM node:20.14.0

# 필수 빌드 도구 설치 (python3, g++, make)
RUN apt-get update && apt-get install -y \
    python3 \
    g++ \
    make

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./

# node_modules 및 package-lock.json 삭제 후 의존성 설치
RUN rm -rf node_modules package-lock.json && npm install --platform=linux

# 나머지 소스 파일 복사
COPY . .

# 빌드 실행
RUN npm run build

# Vite 서버가 외부에서 접근 가능하도록 --host 플래그 추가
ENV VITE_HOST=0.0.0.0

# Vite 기본 포트
EXPOSE 5173

# 개발 서버 실행 (Vite를 --host로 실행)
CMD ["npm", "run", "dev", "--", "--host"]
