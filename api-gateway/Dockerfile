FROM eclipse-temurin:17.0.11_9-jre-alpine AS build

WORKDIR /app
COPY build/libs/*-boot.jar ./api-gateway.jar
RUN java -Djarmode=layertools -jar api-gateway.jar extract --destination extracted

FROM eclipse-temurin:17.0.11_9-jre-alpine

ARG UNAME=ciy
ARG GNAME=ciy

RUN addgroup -S ${GNAME} && \
    adduser -S -G ${GNAME} ${UNAME}
USER ${UNAME}
WORKDIR /home/${UNAME}/api-gateway

COPY --from=build --chown=${UNAME} /app/extracted/dependencies/ ./
COPY --from=build --chown=${UNAME} /app/extracted/spring-boot-loader/ ./
COPY --from=build --chown=${UNAME} /app/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=${UNAME} /app/extracted/application/ ./

CMD java org.springframework.boot.loader.launch.JarLauncher