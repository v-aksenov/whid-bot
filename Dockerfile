FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} whid-bot.jar
ENTRYPOINT ["java","-jar","/whid-bot.jar"]