FROM maven:3.6.2-jdk-13 as builder

ENV M2_HOME /usr/share/maven

COPY ./pom.xml /usr/work/pom.xml
COPY ./lib /usr/work/lib/
COPY ./settings /usr/work/settings/
COPY ./scripts /usr/work/scripts
COPY ./src /usr/work/src/
COPY ./target /usr/work/target/
COPY ./pom.xml /usr/work/pom.xml

RUN cd /usr/work && mvn package -Dmaven.test.skip

