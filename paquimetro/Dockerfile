FROM eclipse-temurin:17.0.2_8-jdk-alpine
RUN mkdir /opt/app
EXPOSE 8080
COPY target/parquimetro-0.0.1-SNAPSHOT.jar /opt/app/japp.jar
CMD ["java", "-jar", "/opt/app/japp.jar"]
