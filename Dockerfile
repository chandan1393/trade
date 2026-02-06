# -------- Stage 1: Build --------
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- Stage 2: Run --------
FROM eclipse-temurin:11-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","app.jar"]
