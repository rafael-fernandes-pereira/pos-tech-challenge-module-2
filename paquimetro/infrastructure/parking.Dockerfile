FROM eclipse-temurin:17.0.2_8-jre-alpine

# Configuração do banco de dados
ENV SPRING_DATA_MONGODB_URI="mongodb://parquimetro:parquimetro@localhost:27017/admin"
ENV SPRING_DATA_MONGODB_DATABASE="parquimetro"

RUN mkdir /opt/app
EXPOSE 8080
COPY ../target/parquimetro-0.0.1-SNAPSHOT.jar /opt/app/japp.jar

# Comando modificado para incluir parâmetros de configuração do Spring Boot
CMD ["java", "-jar", "/opt/app/japp.jar", "--spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}", "--spring.data.mongodb.database=${SPRING_DATA_MONGODB_DATABASE}"]
