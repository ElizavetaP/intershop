# Используем официальный образ OpenJDK 21
FROM openjdk:21-jdk-slim

# Метаданные образа
LABEL maintainer="intershop-app"
LABEL description="Spring Boot Intershop Application"

# Создаем рабочую директорию
WORKDIR /app

# Копируем Gradle wrapper и файлы конфигурации
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Копируем исходный код
COPY src src

# Делаем gradlew исполняемым
RUN chmod +x ./gradlew

# Собираем приложение
RUN ./gradlew bootJar --no-daemon

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "build/libs/intershop-0.0.1-SNAPSHOT.jar"]