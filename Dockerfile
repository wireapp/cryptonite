FROM dejankovacevic/bots.runtime:latest

COPY target/crypto.jar /opt/crypto/crypto.jar
COPY crypto.yaml       /etc/crypto/crypto.yaml

WORKDIR /opt/crypto

