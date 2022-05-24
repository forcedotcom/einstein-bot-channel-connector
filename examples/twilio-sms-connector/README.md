# Twilio SMS Connector

This connector allows Einstein chatbots to be accessed using text messages. It uses Twilio's services to forward messages to and from the demo connector.

## Pre-requisites

Before you can run this app, you need to

* [Create a Connected App to Access Einstein Bot APIs](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-1:-create-a-connected-app)
* [Configure an Einstein Bot](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-2:-configure-an-einstein-bot)
* Create a Twilio account, claim a phone number, and configure the corresponding webhook to point to your web-service url.
* Copy [application.example.properties](src/main/resources/application.properties)
  to `application.properties` and update according to your setup.

## Einstein Bot setup
Configure following properties in `application.properties` to properly connect with Einstein bot. This connector is setup to work with only one Einstein bot.
1. `sfdc.einstein.bots.force-config-endpoint`: Salesforce instance endpoint. This can be obtained from connections screen in bots setup page. Please refer to developer [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html)
2. `sfdc.einstein.bots.orgId`: Salesforce OrgId (18 chars)
3. `sfdc.einstein.bots.botId`: Salesforce bot ID. Refer to this [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/get-started.html#begin-a-new-session) on how to get this ID.
   Other properties related to connected app should be configured as explained [here](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/get-started.html#begin-a-new-session).

## Twilio Setup
* Create an account at [Twilio] (www.twilio.com) and copy your account's SID and authorization token from `API keys and tokens` section of Settings.
1. `twilio.account-sid`: required to use the REST API. 
2. `twilio-auth-keys`: set it to you account's auth token.
* In Twilio's phone number settting, configure the webhook corresponding to the section `A MESSAGE COMES IN` to type `HTTP GET` and point it to your web-service url.
* [NGrok](ngrok.com) can be used to expose the local server ports over the internet if a public url is not available for the web-service. 
* Run spring boot application using maven ` mvn spring-boot:run`

