# 베이스 이미지로 node:20-alpine 사용
FROM node:20-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 필요 패키지 설치
RUN apk add --no-cache libc6-compat

# package.json과 package-lock.json 복사
COPY package*.json ./

# 의존성 설치
RUN npm install

# 나머지 소스 파일 복사
COPY . .

# 타입스크립트 빌드
RUN npm run build

# 컨테이너의 포트 열기
EXPOSE 5173

# 개발 서버 실행
CMD ["npm", "run", "dev"]
