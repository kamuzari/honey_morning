# 1단계: 빌드 단계
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon --stacktrace

COPY src ./src

RUN ./gradlew clean build --no-daemon --stacktrace -x test

# 2단계: 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]