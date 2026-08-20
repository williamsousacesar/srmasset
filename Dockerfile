# Build stage
FROM maven:latest AS builder

WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

RUN chmod +x mvnw && \
    mvn dependency:resolve

COPY src src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
