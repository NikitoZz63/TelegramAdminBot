FROM openjdk:21-jdk-slim

WORKDIR /app

RUN mkdir -p logs

ADD build/libs/adminTGbot-1.0-SNAPSHOT.jar adminBot.jar

EXPOSE 5500

CMD ["java", "-jar", "adminBot.jar"]