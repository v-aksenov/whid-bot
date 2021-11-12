FROM openjdk:17-alpine
COPY target/whid-bot-0.0.1-SNAPSHOT.jar /whid-bot.jar
ENTRYPOINT ["java","-jar","/whid-bot.jar"]