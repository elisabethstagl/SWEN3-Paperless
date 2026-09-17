FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /src
# copy Maven configuration
COPY pom.xml .
# download dependencies
RUN mvn -q dependency:go-offline
# copy source code
COPY src ./src
# build application
RUN mvn package -DskipTests


FROM eclipse-temurin:25-jre
WORKDIR /app
# copy compiled jar from build stage
COPY --from=build /src/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]