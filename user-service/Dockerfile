FROM eclipse-temurin:17-jdk@sha256:ab4bbe391a42adc8e590d0c54b3ca7903cbc3b62a3e3b23ac8dce94ebfef6b9e AS build
ENV USER_SERVICE_ENV=prod

WORKDIR /app
COPY service/build/libs/*-boot.jar ./user-service.jar
RUN java -Djarmode=layertools -jar user-service.jar extract --destination extracted

FROM eclipse-temurin:17-jre-alpine@sha256:10ad0bcc8eef2729dac7fe5938ef615c7ac46eac9016163ca01986715df4fcd8

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