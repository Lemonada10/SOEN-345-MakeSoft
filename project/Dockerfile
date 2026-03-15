# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE $PORT

# Render sets PORT; Spring Boot reads it via server.port=${PORT:8080}
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
