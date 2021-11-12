FROM openjdk:11-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=whid-bot-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} whid-bot.jar
ENTRYPOINT ["java","-jar","/whid-bot.jar"]