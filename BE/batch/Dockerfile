FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY common ./common
COPY batch ./batch

RUN chmod +x gradlew && ./gradlew :batch:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/batch/build/libs/batch-0.0.1-SNAPSHOT-boot.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
