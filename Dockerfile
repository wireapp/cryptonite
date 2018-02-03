FROM dejankovacevic/bots.runtime:latest

COPY target/cryptonite.jar /opt/cryptonite/cryptonite.jar
COPY cryptonite.yaml       /etc/cryptonite/cryptonite.yaml

WORKDIR /opt/cryptonite

