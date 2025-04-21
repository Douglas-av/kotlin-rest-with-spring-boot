FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y libcap2-bin && \
    setcap 'cap_net_bind_service=+ep' /usr/lib/jvm/java-21-jdk-slim-amd64/bin/java && \
    rm -rf /var/lib/apt/lists/*
USER 1000
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 80
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]