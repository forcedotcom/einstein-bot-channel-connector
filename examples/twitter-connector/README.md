# Twitter Connector

This application is built using Einstein Bots Channel Connector Archetype. It connects Einstein bots to twitter. 

## Pre Requisites

Before you can run this app, you need to
* [Create a Connected App to Access Einstein Bot APIs](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-1:-create-a-connected-app)
* [Configure an Einstein Bot](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html#step-2:-configure-an-einstein-bot)
* [Create a twitter account](https://twitter.com)
* [Sign up for developer account](https://developer.twitter.com/en/docs/twitter-api/getting-started/getting-access-to-the-twitter-api)

## Einstein Bot setup
Configure following properties in `application.properties` to properly connect with Einstein bot. This connector is setup to work with only one Einstein bot.
1. `sfdc.einstein.bots.force-config-endpoint`: Salesforce instance endpoint. This can be obtained from connections screen in bots setup page. Please refer to developer [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/prerequisites.html)
2. `sfdc.einstein.bots.orgId`: Salesforce OrgId (18 chars)
3. `sfdc.einstein.bots.botId`: Salesforce bot ID. Refer to this [documentation](https://developer.salesforce.com/docs/service/einstein-bot-api/guide/get-started.html#begin-a-new-session) on how to get this ID.
Other properties related to connected app should be configured as explained [here](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/master/channel-connector-starter#configure-your-application).

## Twitter setup
### Auth setup
Once you have setup developer account then create a project and generate appropriate keys and tokens. Map them to appropriate properties in `application.properties`: 
1. OAuth1 
   1. Consumer Key: `twitter.oauth.consumerKey`
   2. Consumer Secret: `twitter.oauth.consumerSecret`
   3. Access token: `twitter.oauth.accessToken`
   4. Access token secret: `twitter.oauth.accessTokenSecret`
2. Bearer Auth
   1. Bearer Token: `twitter.bearerToken`

Access token and access token secret generated on developer portal doesn’t work when you try to create a tweet. You will get `403 - Forbidden error`. 
Generate access token and access token secret using following steps:
1. Make sure that your app has both READ and WRITE permissions. You can do that by turning ON User authentication settings->OAuth1.0a settings and configuring the permissions. 
2. Install twurl (https://developer.twitter.com/en/docs/tutorials/using-twurl)
3. Run `twurl autorize --consumer-key ... --consumer-secret ...`. Follow the onscreen instructions:
    1. visit the link output by above command. This will ask you (as a user - need to be logged into twitter) to authorize app.
    2. Once you authorize it will give you a PIN.
    3. Come back to terminal and enter that PIN.
    4. `twurl` will store access token and access token secret in `~/.twurlrc`. Use that access token and access token secret when [configuring](#auth-setup) this connector.

### User setup
This twitter connector works with only one twitter account and it should be configured in `application.properties`
1. `twitter.user.name`: This is the screen name or twitter handle for the account (e.g. @Salesforce)
2. `twitter.user.id`: This is the ID of the user. It can be obtained by hitting `2/users/by/username/:username` endpoint of twitter (here `:username` is twitter handle without `@` sign - so if your twitter handle is `@example` then `:username` will be `example`). You can get this by hitting this endpoint from [Postman collection](https://www.postman.com/twitter/workspace/twitter-s-public-workspace/collection/9956214-784efcda-ed4c-4491-a4c0-a26470a67400?ctx=documentation) published by twitter.
3. `twitter.rule.ids`: If you have any streaming rules configured using `2/tweets/search/stream/rules` endpoint then get those rule ids and configure this property. This is comma separated list. If not configured then this connector will create a new rule that filters the stream by user mentions.
