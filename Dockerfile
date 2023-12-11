FROM eclipse-temurin:17-jdk AS base-image
RUN apt-get update && apt-get install -y git
RUN git config --global user.name "lemphis"
RUN git config --global user.email "lemphis@gmail.com"

FROM base-image AS builder
ARG DISABLE_CACHE
RUN echo $DISABLE_CACHE
RUN git clone https://github.com/lemphis/spring-template.git
RUN chmod 700 gradlew
RUN ./gradlew build --no-build-cache --no-daemon --stacktrace --warning-mode=all --exclude-task test

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=builder /spring-template/build/libs/template.jar /app/template.jar
WORKDIR /app
ENTRYPOINT [ "java", "-jar", "template.jar" ]
CMD [ "--spring.profiles.active=dev" ]
