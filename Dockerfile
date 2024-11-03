# Multistage dockerfile
# Purpose of first layer is to build and generate target.jar file
FROM ubuntu:latest AS builder

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .
RUN apt-get install maven -y

RUN mvn clean install -DskipTests

# Purpose of second layer is to extract the layers located in .jar file
FROM eclipse-temurin:21-jre AS build

WORKDIR application
COPY --from=builder /target/*.jar application.jar

RUN java -Djarmode=layertools -jar application.jar extract

#Purpose of third layer is to copy the layers previously extracted to the container
FROM eclipse-temurin:21-jre

WORKDIR application
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]

EXPOSE 8080