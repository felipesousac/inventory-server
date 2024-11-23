# Multistage dockerfile
# Build and generate target.jar file
FROM ubuntu:latest AS builder

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .
RUN apt-get install maven -y

RUN mvn clean install -DskipTests

# Extract the layers located in target.jar file
FROM eclipse-temurin:21-jre AS build

WORKDIR application
ARG JAR_FILE=/target/*.jar
COPY --from=builder ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers

# Copy the layers previously extracted to the container
FROM eclipse-temurin:21-jre

WORKDIR application
COPY --from=build application/application/dependencies/ ./
COPY --from=build application/application/spring-boot-loader/ ./
COPY --from=build application/application/snapshot-dependencies/ ./
COPY --from=build application/application/application/ ./

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]

EXPOSE 8080