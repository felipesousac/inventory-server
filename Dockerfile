# Multistage dockerfile
# Purpose of first layer named as builder is to extract the layers located in .jar file
FROM eclipse-temurin:21-jre AS builder
WORKDIR application
RUN apt-get install maven -y
RUN mvn clean package -DskipTests
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

#Purpose of second layer is to copy the layers previously extracted to the container
FROM eclipse-temurin:21-jre
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]

EXPOSE 8080