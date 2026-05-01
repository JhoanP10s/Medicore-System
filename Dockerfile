FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src src

# The official Maven image defines MAVEN_CONFIG=/root/.m2. The Maven Wrapper
# script treats MAVEN_CONFIG as CLI arguments, so it can be misread as a
# lifecycle phase. Keep it empty when using ./mvnw inside this image.
ENV MAVEN_CONFIG=""
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
