ARG BASE_IMAGE=adoptopenjdk:16-jre-hotspot
FROM ${BASE_IMAGE}

COPY build/libs/*-all.jar ./app.jar

EXPOSE 8080
CMD java ${JAVA_OPTS} -jar app.jar
