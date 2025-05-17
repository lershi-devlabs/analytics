FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

COPY target/analytics-sb-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN apk add --no-cache bash

EXPOSE 8080

ENTRYPOINT ["/wait-for-it.sh", "db:3306", "-t", "60", "--", "java", "-jar", "app.jar"] 