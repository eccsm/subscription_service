FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/newsletter-0.0.1-SNAPSHOT.jar newsletter.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/newsletter.jar"]
