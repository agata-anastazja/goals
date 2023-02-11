FROM eclipse-temurin:19-alpine

COPY target/api-0.1.0-SNAPSHOT.jar .

WORKDIR /app

COPY . /app

# Build a jar
RUN clojure -T:build ci

RUN cp target/*-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar", "target/api-0.1.0-SNAPSHOT.jar"]
EXPOSE 8080
