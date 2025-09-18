# --------Сборка (builder) образа JDK + Gradle
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Копируем корневые файлы проекта
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Копируем OpenAPI спецификации (для генерации кода)
COPY api-specs ./api-specs

# Копируем файлы модуля payment-service
COPY payment-service/build.gradle ./payment-service/
COPY payment-service/src ./payment-service/src

# Устанавливаем права на gradlew и кешируем зависимости
RUN chmod +x ./gradlew && ./gradlew :payment-service:dependencies --no-daemon || true

# Собираем JAR для payment-service модуля
RUN ./gradlew :payment-service:bootJar --no-daemon

# --------Рантайм (базовый образ только с JRE)
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Копируем только собранный JAR из build stage
COPY --from=build /workspace/payment-service/build/libs/payment-service-*.jar /app/app.jar

# Открываем порт для сервиса
EXPOSE 8081

# Настройки JVM
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
