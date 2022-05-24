# Einstein Bots On Slack
This repo contains code that creates a link between Slack and Einstein Bots. 

The code is designed to be deployed as a [Heroku](https://dashboard.heroku.com) app.

This README covers details on how to set up the connection between Slack and Einstein Bots.

## Pre-requisites

Before we get started on the setup, the following tooling is necessary
 1. Heroku CLI
    
    The Heroku CLI provides the best Heroku Developer experience.

    To install it, run the following command:

    ```brew tap heroku/brew && brew install heroku```
 2. Slack Workspace
 
    You will need to have a workspace where you can set up the Slack Application.

    This will involve getting in touch with your Slack Admin to provide you with developer access to a workspace where you can install a Slack Application.
    
 3. Einstein Bot

    This document does not cover how to set up an Einstein Bot so make sure you have a bot ready to plug in to slack
    
    
## Setup

The setup is divided into 3 parts:
 - Slack App setup
 - Einstein Bot setup
 - Heroku App setup
 
### Slack App setup 

This Section covers all the setup needed in Slack to allow the code in this repo to talk to a Slack app in your desired workspace

  - Go to [api.slack.com](https://api.slack.com)
  - On the top right, Click on ```Create New App```
  - Select ```From App Manifest```
  - Select the right workspace for your Slack App to be installed in
  - Paste the following yaml for the basic setup (you can edit it later if you need to add more permissions)
    ```yaml
    _metadata:
      major_version: 1
      minor_version: 1
    display_information:
      name: <<<Bot Name>>>
    features:
      bot_user:
        display_name: <<<Bot Name>>>
        always_online: true
      slash_commands:
        - command: /reply-in-thread-toggle
          url: https://<<<Heroku App>>>.herokuapp.com/slack/events
          description: Toggle reply in thread
          usage_hint: /reply-in-thread-toggle
          should_escape: false
    oauth_config:
      scopes:
        user:
          - im:write
          - im:history
          - im:read
          - mpim:history
          - mpim:read
          - mpim:write
        bot:
          - app_mentions:read
          - channels:history
          - channels:read
          - chat:write
          - chat:write.customize
          - chat:write.public
          - commands
          - incoming-webhook
          - im:history
          - im:read
          - im:write
          - mpim:history
          - mpim:read
          - mpim:write
          - groups:history
          - groups:read
          - groups:write
    settings:
      event_subscriptions:
        request_url: https://<<<Heroku App>>>.herokuapp.com/slack/events
        bot_events:
          - app_mention
          - member_joined_channel
          - message.channels
          - message.groups
          - message.im
          - message.mpim
      interactivity:
        is_enabled: true
        request_url: https://<<<Heroku App>>>.herokuapp.com/slack/events
      org_deploy_enabled: false
      socket_mode_enabled: false
      is_hosted: false
      token_rotation_enabled: false 
    ```
     
     Make sure to fill in values of ```<<<Bot Name>>>```
      For now, you can leave ```<<<Heroku App>>>``` Alone. If the yaml errors out, provide a dummy url.
      
- hit ```Create``` and let Slack create the app for you
- Head on over to the ```OAuth & Permissions``` tab on the left
- Click on ```Install to Workspace```
- Select the channel you created in the pre-requisite steps and say ```Allow```
- You should now have OAuth tokens available in Slack.

 This slack app will have all the permissions and setup needed to run your the vanilla code provided in this repo.

### Einstein Bots Setup

This Section will cover the setup needed in Einstein Bots to enable the code in this repo to talk to Einstein Bots.

The desired means of achieving this will be to setup another OAuth token and secret.

Configure following properties in `application.properties` to properly connect with Einstein bot. 

This connector is setup to work with only one Einstein bot.

1. `sfdc.einstein.bots.force-config-endpoint`: Salesforce instance endpoint. This can be obtained from connections screen in bots setup page. Please refer to developer [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html)
2. `sfdc.einstein.bots.orgId`: Salesforce OrgId (18 chars)
3. `sfdc.einstein.bots.botId`: Salesforce bot ID. Refer to this [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/get-started.html#begin-a-new-session) on how to get this ID.
   Other properties related to connected app should be configured as explained [here](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/master/channel-connector-starter#configure-your-application).


### Heroku App Setup

This section will cover how to deploy the code in this repository to Heroku.

- Clone the repository ```git clone https://git.soma.salesforce.com/chatbots/slack-demo-bot```
- Copy your Chatbot OAuth primary key (server.key file created in the Chatbot setup) to src/main/resources. Any file that's put here is available in the Heroku dyno (running container) under ```/app/target/classes/``` -- You'll see this path in the config vars
- Run the following command to convert it into a format that is accepted by the Java code ```openssl x509 -outform der -in server.key -out private_key.der```
- Go into the cloned directory ```cd slack-demo-bot```
- Create a Heroku app (requires Heroku CLI) ```heroku create```
- Open the pom.xml and at the bottom update the app name under ```<appName>YOUR-APP-NAME-HERE</appName>``` with the app name that was just created.
- Send the app to Heroku ```mvn clean heroku:deploy```
- Go to [Heroku dashboard](https://dashboard.heroku.com)
- Find your app (you can mark it as favorite so it's easier to find in the future)
- Switch to the ```Settings``` tab
- Click on ```Reveal Config Vars```
- Set up the following Config Vars needed for the app to function (they can also be set up in application.properties file)

| Name  	| Description	                                                                                                                                                                                                                                                                                                              | Example  	|
|---	|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---	|
| BOT_RUNTIME_URL  	| The base URL for Chatbot Runtime.  	                                                                                                                                                                                                                                                                                      |  https://runtime-api-na-west.stg.chatbots.sfdc.sh 	|
| BOT_ID  	| The Salesforce ID of the bot that's being used. This can be obtained from the URL of the bot builder page of the bot you are building   	                                                                                                                                                                                 |   0XxRM00000001aZ0AQ	|
| CONNECTED_APP_ID  	| ID of the Connected app you set up in the previous section for OAuth 	                                                                                                                                                                                                                                                    |   3MVG9qKMKuRGRcbvcbIIXp10B7zSBvx.NOWQt0UlNx0BGwWtLF2Gzzlb0GB_ALlwga1JbYzYD4hkKrBi5aBRU	|
| CONNECTED_APP_SECRET  	| Secret of the Connected app you setup for API Connection. 	                                                                                                                                                                                                                                                    |   3MVG9qKMKuRGRcbvcbIIXp10B7zSBvx.NOWQt0UlNx0BGwWtLF2Gzzlb0GB_ALlwga1JbYzYD4hkKrBi5aBRU	|
| FORCE_CONFIG_ENDPOINT  	| The Salesforce endpoint that the heroku app will need to connect to. This is where you made the bot  	                                                                                                                                                                                                                    |   https://na44.stmfa.stm.salesforce.com:8443	|
| BOT_INTEGRATION_NAME  	| The name of the integration that will be used. In our case, most likey this will be the api integration unless there are some special exception scenarios 	                                                                                                                                                               |  sfdc.chatbot.api 	|
| LOGIN_END_POINT  	| The URL to hit to obtain an OAuth Token 	                                                                                                                                                                                                                                                                                 |  https://login.salesforce.com 	|
| ORG_ID  	| The ORG ID where the bot is configured. This can be obtained by going to setup and selecting Company information from the left in your Salesforce Org. Its required to be 18 digits so you can use [https://www.adminbooster.com/tool/15to18](https://www.adminbooster.com/tool/15to18) to convert from 15 to 18 digits 	 |   00DRM0000006k892AA	|
| OAUTH_PRIVATE_KEY_FILE_PATH  	| Path to the private key needed to get OAuth Tokens if you've put this key in the resources folder as per instructions, it should be available in the same place as the example here 	                                                                                                                                     |  /app/target/classes/private_key.der 	|
| REPLY_IN_THREAD_CHANNELS  	| Comma separated Channel IDs where the bot will reply in thread by default -- Can be blank	                                                                                                                                                                                                                                |   C02MLJBEL1Z,C02MG4W7VFH	|
| SLACK_BOT_TOKEN  	| The OAuth Token you obtained from Slack	                                                                                                                                                                                                                                                                                  |  xoxb-8182420140121-2166001931781-fp6QpJ9zD3H7EFR6sdbPuINU 	|
| SLACK_SIGNING_SECRET  	| The signing secret you obtained from Slack 	                                                                                                                                                                                                                                                                              |   3244gg912464d4dbd1830e86fdf4ad9d	|
| USE_BUTTONS  	| When the chatbot api returns a choice message, these can be rendered in slack as drop down menus or as choice buttons. This config is a setting to toggle between the two	                                                                                                                                                |   true	|
| SFDC_USER_ID  	| The user ID for the login that will be used to obtain oauth tokens (should be the user that created the connected app in Salesforce)	                                                                                                                                                                                     |   pprem@salesforce.com	|
| CACHE_TTL_SECS  	| Time to keep data alive in Cache	                                                                                                                                                                                                                                                                                         |   259140	|

Once all these vars are set up, make sure you change the URLs in the slack manifest to point to your heroku app and you are good to go.

You can test by sending a message in slack to the bot!

### NOTE

None of the keys mentioned anywhere in this repo work. They are just samples with the correct number of characters

There is a Makefile in this repo that can help with some commands.
