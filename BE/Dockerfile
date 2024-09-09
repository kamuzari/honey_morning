# 1단계: 빌드 단계
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

# Gradle과 관련된 파일만 먼저 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x gradlew

# 캐시를 활용한 종속성 다운로드
RUN ./gradlew dependencies --no-daemon || return 0

# 나머지 소스 파일 복사
COPY . .

# 실제 애플리케이션 빌드
RUN ./gradlew clean build --no-daemon --stacktrace

# 2단계: 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
