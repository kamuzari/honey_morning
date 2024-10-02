FROM node:20.14.0

# 필수 빌드 도구 설치 (python3, g++, make)
RUN apt-get update && apt-get install -y \
    python3 \
    g++ \
    make \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# package.json과 package-lock.json (있는 경우) 복사
COPY package*.json ./

# node_modules 및 package-lock.json 삭제 후 의존성 설치
RUN rm -rf node_modules package-lock.json && npm install --platform=linux --legacy-peer-deps

# 소스 코드 복사
COPY . .

# 환경 변수 설정
ARG VITE_BASE_URL
ARG VITE_PROJECT_DATA_URL
ENV VITE_BASE_URL=${VITE_BASE_URL}
ENV VITE_PROJECT_DATA_URL=${VITE_PROJECT_DATA_URL}

# 프로덕션 빌드
RUN npm run build

# Vite 서버가 외부에서 접근 가능하도록 --host 플래그 추가
ENV VITE_HOST=0.0.0.0

EXPOSE 5173

# 개발 서버 실행 (Vite를 --host로 실행)
CMD ["npm", "run", "dev", "--", "--host"]