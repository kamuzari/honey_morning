# 빌드 단계
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon --stacktrace

COPY src ./src

RUN ./gradlew clean build --no-daemon --stacktrace -x test

# 실행 단계
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# 설정 파일을 위한 디렉토리 생성
RUN mkdir -p /app/config

EXPOSE 8081

# 외부 설정 파일을 우선적으로 사용하도록 ENTRYPOINT 수정
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/config/application.properties,classpath:/application.properties"]