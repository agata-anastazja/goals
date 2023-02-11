FROM eclipse-temurin:19-alpine

RUN cp target/*-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar", "target/api-0.1.0-SNAPSHOT.jar"]
EXPOSE 8080
