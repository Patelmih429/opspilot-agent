FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -ntp dependency:go-offline
COPY src src
RUN ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S opspilot && adduser -S opspilot -G opspilot
WORKDIR /app
COPY --from=build /workspace/target/opspilot-agent-*.jar app.jar
USER opspilot
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
