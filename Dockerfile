FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 10000
ENTRYPOINT ["sh","-c","java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar /app/app.war"]
