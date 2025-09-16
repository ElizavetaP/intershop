# --------Сборка (builder) образа JDK + Gradle
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Копируем корневые файлы проекта
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Копируем файлы модуля intershop-main
COPY intershop-main/build.gradle ./intershop-main/
COPY intershop-main/src ./intershop-main/src

# Устанавливаем права на gradlew и кешируем зависимости
RUN chmod +x ./gradlew && ./gradlew :intershop-main:dependencies --no-daemon || true

# Собираем JAR для intershop-main
RUN ./gradlew :intershop-main:bootJar --no-daemon

# --------Рантайм (базовый образ только с JRE)
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Копируем только собранный JAR из build stage
COPY --from=build /workspace/intershop-main/build/libs/intershop-main-*.jar /app/app.jar

# Открываем порт для веб-приложения
EXPOSE 8080

# Настройки JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
