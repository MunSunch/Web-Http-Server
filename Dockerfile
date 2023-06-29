FROM openjdk:18.0.2.1-slim-buster
WORKDIR /app
COPY ./target/ServerSocket-1.0-SNAPSHOT.jar ./
CMD java -jar ServerSocket-1.0-SNAPSHOT.jar