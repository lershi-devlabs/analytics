FROM eclipse-temurin:24-jdk-alpine as builder

WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/analytics-sb-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN apk add --no-cache bash

EXPOSE 8080

# Wait for database to be ready before starting Spring Boot
ENTRYPOINT /wait-for-it.sh "${MYSQL_HOST:-db}:${MYSQL_PORT:-3306}" -t 60 -- java -jar app.jar 