FROM eclipse-temurin:17.0.2_8-jre-alpine

# Configuração do banco de dados
ENV SPRING_DATA_MONGODB_URI="mongodb://parquimetro:parquimetro@localhost:27017/admin"
ENV SPRING_DATA_MONGODB_DATABASE="parquimetro"

ENV SPRING_MAIL_HOST=localhost
ENV SPRING_MAIL_PORT=8025
ENV SPRING_MAIL_USERNAME=setusername
ENV SPRING_MAIL_PASSWORD=setpassword

ENV SERVER_PORT=8080


RUN mkdir /opt/app
EXPOSE 8080
EXPOSE 8081
COPY ../target/parquimetro-0.0.1-SNAPSHOT.jar /opt/app/japp.jar

# Comando modificado para incluir parâmetros de configuração do Spring Boot
CMD ["java", "-jar", "/opt/app/japp.jar", "--spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}", "--spring.data.mongodb.database=${SPRING_DATA_MONGODB_DATABASE}", "--spring.mail.host=${SPRING_MAIL_HOST}", "--spring.mail.port=${SPRING_MAIL_PORT}", "--spring.mail.username=${SPRING_MAIL_USERNAME}", "--spring.mail.password=${SPRING_MAIL_PASSWORD}", "--server.port=${SERVER_PORT}"]
