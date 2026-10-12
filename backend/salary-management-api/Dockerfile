FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn ./.mvn
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src ./src
RUN ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/target/salary-management-api-0.0.1-SNAPSHOT.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-jar", "/app/app.jar"]
