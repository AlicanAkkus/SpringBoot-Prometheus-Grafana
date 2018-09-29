#!/usr/bin/env bash

function up(){
    mvn clean install -Dmaven.test.skip=true && docker build -t app -f docker/Dockerfile . && docker-compose -f docker/docker-compose.yml up -d
}

function down(){
    docker-compose -f docker/docker-compose.yml down
}

case "${1}" in
    "up")
        up;;
    "down")
        down;;
esac