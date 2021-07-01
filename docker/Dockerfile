# Caricamento del layer base per l'esecuzione del progetto
FROM openjdk:8-jdk-alpine

EXPOSE 8080

ARG JAVA_OPTS
RUN apk update && \
  apk add --no-cache libc6-compat && \
  ln -s /lib/libc.musl-x86_64.so.1 /lib/ld-linux-x86-64.so.2
ENV JAVA_OPTS=$JAVA_OPTS
ENV Xmx=-Xmx8192m Xss=-Xss512m

# Add a volume pointing to /volume
VOLUME /volume

# The application's jar file
ARG JAR_FILE=target/lda-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} lda.jar

# Run the jar file
ENTRYPOINT exec java $JAVA_OPTS $Xmx $Xss -Djava.security.egd=file:/dev/./urandom -jar lda.jar
