FROM eclipse-temurin:8u402-b06-jre-alpine

RUN mkdir -p /output

ADD http://nilhcem.github.io/FakeSMTP/downloads/fakeSMTP-latest.zip /fakeSMTP-latest.zip

RUN unzip /fakeSMTP-latest.zip

VOLUME /output

EXPOSE 25

ENTRYPOINT ["java","-jar","/fakeSMTP-2.0.jar","--background", "--output-dir", "/output", "--port", "25", "--start-server"]
