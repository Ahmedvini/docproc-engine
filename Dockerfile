FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests compile

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/classes ./classes
EXPOSE 8080
CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "classes", "com.docproc.app.Main", "--server"]
