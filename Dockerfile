FROM clojure:latest

ENV GOALS_ENV = prod

WORKDIR /app

COPY . /app

CMD ["clojure", "-M:run-m"]

EXPOSE 8080