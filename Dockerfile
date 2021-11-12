FROM openjdk:11.0.4-jre-slim-buster
COPY target/whid-bot-0.0.1-SNAPSHOT.jar /whid-bot.jar
ENTRYPOINT ["java","-jar","/whid-bot.jar"]