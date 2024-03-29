FROM gradle:8-jdk17-alpine as builder
LABEL authors="Jan biały - yachoo2606"

WORKDIR /build
COPY src src
COPY gradle gradle
COPY *.gradle ./
COPY *.properties ./
COPY *.config ./


RUN gradle customFatJar



FROM openjdk:17-jdk-slim-buster

COPY --from=builder /build/build/libs/*.jar /cassandraproject/executables/cassandraproject.jar

ADD dockerLib/cqlsh.tar.gz /cassandraproject/executables/

WORKDIR /cassandraproject/executables/
RUN mkdir "logs"

RUN apt-get -y update && \
#    apt-get -y upgrade && \
    apt-get -y install python3-pip dos2unix

COPY *.sh ./
RUN dos2unix wait_for_cassandra.sh
RUN chmod +x wait_for_cassandra.sh

# Assuming your Dockerfile has an ENTRYPOINT specified
ENTRYPOINT ["./wait_for_cassandra.sh"]
