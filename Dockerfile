FROM ubuntu:16.04

RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8

RUN apt-get update && apt-get install -y openjdk-8-jre


COPY target/scala-2.12/highload_sandbox-assembly-0.1.jar /home/app.jar
EXPOSE 80


CMD su && java -jar /home/app.jar
