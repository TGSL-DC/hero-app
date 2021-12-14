ARG BASE_IMAGE=eclipse-temurin:17
FROM ${BASE_IMAGE}

RUN adduser --system --group --no-create-home appuser

WORKDIR /app

COPY build/libs/*-all.jar ./app.jar

USER appuser

EXPOSE 8080
CMD java ${JAVA_OPTS} -jar app.jar
