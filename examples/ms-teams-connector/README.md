# Einstein Bots On Microsoft Teams
This example illustrates the use of channel connector framework to connect Microsoft Teams users to Einstein Bots. 

The code is designed to be deployed as a [Heroku](https://dashboard.heroku.com) app but can also be executed in a local development environment with ngrok.

This document covers the process to set up and deploy the Microsoft Teams connector.

## Pre-requisites


    
1. Einstein Bots

    You must have an active Org with Chat or Messaging licenses to have access to Einstein Chatbots.

2. Microsoft Teams Account

   You will need to have an active Microsoft Teams account. You can sign up for a free account [here.](https://www.microsoft.com/en-us/microsoft-teams/group-chat-software)
    
## Setup

The setup is divided into 3 parts:
 - Einstein Bots setup
 - Update configurations of the demo connector to point to your environment.
 - Microsoft Teams setup
 

### Einstein Bots Setup

Before you can run this app, you need to

* [Create a Connected App to Access Einstein Bot APIs](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-1:-create-a-connected-app)
* [Configure an Einstein Bot](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-2:-configure-an-einstein-bot)

### Update Configuration

Configure the demo applications to use your newly created test bot. This can be done by setting the following properties in `application.properties`. 

1. `sfdc.einstein.bots.force-config-endpoint`: Salesforce instance endpoint. This can be obtained from connections screen in bots setup page. Please refer to developer [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html)
2. `sfdc.einstein.bots.orgId`: Salesforce OrgId (18 chars)
3. `sfdc.einstein.bots.botId`: Salesforce bot ID. Refer to this [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/get-started.html#begin-a-new-session) on how to get this ID.
   Other properties related to connected app should be configured as explained [here](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/master/channel-connector-starter#configure-your-application).

   
### Microsoft Teams setup

A Microsoft Teams account must already have been created and set up. Please refer to Microsoft's documentation to set up an account. In the following, we will set up an outgoing webhook from Microsoft Teams which will point to the connector end-point defined in this example. For the latest instructions, please refer to the [official instructions here](https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-outgoing-webhook). From Teams desktop application,
- Select Teams from the left pane. 
- In the Teams page, select the required team for which you would like to create an Outgoing Webhook and select the •••. 
- In the dropdown menu, select Manage team. 
- Select the Apps tab on the channel page. Select `Create an Outgoing Webhook`
- Type the following details in the `Create an Outgoing Webhook` page:
  - Name: A message will be sent to the chatbot if it contains this @mention.
  - Callback URL: The HTTPS endpoint must point to the web url of this demo application e.g. https://your_website.com/bot.
  - Description: Your bot description. It will also appear in the profile card and the team-level App dashboard.
  - Profile Picture: An optional app icon for your chatbot.
- Select Create. The Outgoing Webhook will be added to the current team's channel.

The newly create Outgoing Webhook acts as a bot and search for messages in channels using @mention. The messages will be received by this demo connector and will be forwarded to the Einstein Chatbot.

## Running the example
* Build your application in your desired development environment.
* Deploy your application on Heroku or on your local environment using ngrok. When executing in a local dev environment
  * Execute `ngrok http 8080` to start a ngrok session. It will associate a public url to your local dev server.
  * Run spring boot application using maven `mvn spring-boot:run` to launch your application and listen to endpoint.
  * The webhook's callback URL in Microsoft Teams must be updated to the public url of your application.

