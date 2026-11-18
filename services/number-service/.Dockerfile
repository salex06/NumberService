# === BUILD ===
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# pom-файлы для кэширования зависимостей
COPY pom.xml .
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/number-service/pom.xml services/number-service/pom.xml
COPY platform/pom.xml platform/pom.xml
COPY services/api-gateway/pom.xml services/api-gateway/pom.xml

# Скачивание зависимостей для всех модулей
RUN mvn -B -q dependency:go-offline

# Исходники number-service и его зависимостей
COPY platform/src platform/src
COPY services/number-service/src services/number-service/src

# Сборка number-service вместе с зависимостями
RUN mvn -B -q -pl services/number-service -am package -DskipTests

# === RUNTIME ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /workspace/services/number-service/target/*.jar app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
