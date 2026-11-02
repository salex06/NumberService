# === BUILD ===
FROM maven:3.9-eclipse-temurin-21 AS build

# Установка текущей рабочей директории
WORKDIR /workspace

# Копирование pom-файлов (корневой + сервиса + общий)
COPY pom.xml .
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY platform/pom.xml platform/pom.xml

# Скачивание зависимостей
RUN mvn -B -q -pl services/user-service -am dependency:go-offline

# Копирование исходников
COPY services/user-service/src services/user-service/src

# Сборка модуля и зависимостей
RUN mvn -B -q -pl services/user-service -am package -DskipTests

# === RUNTIME ===
FROM eclipse-temurin:21-jre-alpine

# Установка текущей директории
WORKDIR /app

# Добавление non-root пользователя
RUN addgroup -S app && adduser -S app -G app

# Перенос *.jar-файлов в рабочую директорию
COPY --from=build /workspace/services/user-service/target/*.jar app.jar

# Установка текущего пользователя
USER app

# Используется порт 8080
EXPOSE 8080

# Запуск сервиса
ENTRYPOINT ["java", "-jar", "/app/app.jar"]