FROM eclipse-temurin:20-alpine

COPY  target/*-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
EXPOSE 8080
