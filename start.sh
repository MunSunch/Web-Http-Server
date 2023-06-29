#!/bin/sh
mvn clean install -DskipTests=true assembly:single
docker build --tag=my_server .
docker run -d --rm -p=8082:9999 my_server