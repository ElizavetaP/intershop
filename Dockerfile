# --------Сборка (builder) образа JDK + Gradle
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /src

# Кэшируем зависимости: gradle файлы
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

# Теперь исходники
COPY src ./src

# Собираем fat-jar
RUN ./gradlew --no-daemon clean bootJar

# --------Рантайм (базовый образ только с JRE)
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Копируем только ar и даем имя app.jar
ARG JAR_NAME=intershop-0.0.1-SNAPSHOT.jar
COPY --from=build /src/build/libs/${JAR_NAME} /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
