# Channel Connector Application

This application to built using Channel Connector Archetype work with Einstein Bots API.

## Pre Requisites

Before you can run this app, you need to 

* [Create a Connected App to Access Einstein Bot APIs](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-1:-create-a-connected-app)
* [Configure an Einstein Bot](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-2:-configure-an-einstein-bot)
* Copy [application.example.properties](src/main/resources/application.example.properties) to `application.properties` and update according to your setup. 

## Running this Example

Run spring boot application using maven ` mvn spring-boot:run`

## Health
You run health check by hitting http://localhost:8080/bot/health

## Metrics

To view metrics : http://localhost:8080/actuator/metrics
