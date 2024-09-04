FROM eclipse-temurin:17.0.11_9-jre-alpine AS build

WORKDIR /app
COPY service/build/libs/*-boot.jar ./user-service.jar
RUN java -Djarmode=layertools -jar user-service.jar extract --destination extracted

FROM eclipse-temurin:17.0.11_9-jre-alpine

ENV USER_SERVICE_ENV=prod

ARG UNAME=ciy
ARG GNAME=ciy

RUN addgroup -S ${GNAME} && \
    adduser -S -G ${GNAME} ${UNAME}
USER ${UNAME}
WORKDIR /home/${UNAME}/user-service

COPY --from=build --chown=${UNAME} /app/extracted/dependencies/ ./
COPY --from=build --chown=${UNAME} /app/extracted/spring-boot-loader/ ./
COPY --from=build --chown=${UNAME} /app/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=${UNAME} /app/extracted/application/ ./

CMD java org.springframework.boot.loader.launch.JarLauncher