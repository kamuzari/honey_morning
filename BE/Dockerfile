# 1단계: 빌드 단계
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

# 필요한 파일들을 복사합니다
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# application.properties 파일을 복사합니다
COPY src/main/resources/application.properties ./src/main/resources/application.properties

# 실행 권한을 부여하고 의존성을 다운로드합니다
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon --stacktrace

# 애플리케이션을 빌드합니다
RUN ./gradlew clean build --no-daemon --stacktrace -x test

# 2단계: 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일을 복사합니다
COPY --from=builder /app/build/libs/*.jar app.jar

# application.properties 파일을 복사합니다
COPY --from=builder /app/src/main/resources/application.properties /app/application.properties

# 포트를 노출시킵니다
EXPOSE 8081

# 애플리케이션을 실행합니다
ENTRYPOINT ["java", "-jar", "/app/app.jar"]