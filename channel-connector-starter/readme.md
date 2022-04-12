# Channel Connector Starter

Channel Connector Framework is spring boot starter that simplifies building channel connectors for
the salesforce einstein bot. It sets up required dependencies like caching, authentication, metrics
using spring auto configuration.

## Getting Started

### Add POM dependency

```xml

<dependency>
  <groupId>com.salesforce.chatbot</groupId>
  <artifactId>module-channel-connector-java-starter</artifactId>
  <version>${module-channel-connector-java-starter-version}</version>
</dependency>
```

### Configure your application

Create spring `application.properties` with following properties. You can copy from
this [example](../channel-connector-example/src/main/resources/application.properties).

```properties
#The base URL for Chatbot Runtime.
sfdc.einstein.bots.runtime-url=${BOT_RUNTIME_URL}
#The name of the integration that will be used. This should match the Integration Name used
#when adding connected app API connection to Bot.
sfdc.einstein.bots.integration-name=${BOT_INTEGRATION_NAME}
#OAuth Properties
#Path to the private key needed to get OAuth Tokens.
sfdc.einstein.bots.oauth.private-key-file=${OAUTH_PRIVATE_KEY_FILE_PATH}
#The URL to hit to obtain an OAuth Token
sfdc.einstein.bots.oauth.login-endpoint=${LOGIN_END_POINT}
#ID of the Connected app you setup for API Connection.
sfdc.einstein.bots.oauth.connected-app-id=${CONNECTED_APP_ID}
#Secret of the Connected app you setup for API Connection.
sfdc.einstein.bots.oauth.connected-app-secret=${CONNECTED_APP_SECRET}
#The user ID for the login that will be used to obtain oauth tokens (should be the user that created the connected app in Salesforce)
sfdc.einstein.bots.oauth.user-id=${SFDC_USER_ID}
#Cache properties
#Time to cache information to avoid network requests. For e.g oAuth token will cached for ttlseconds.
sfdc.einstein.bots.cache.ttlseconds=${CACHE_TTL_SECS:259140}
#Optional to use Redis as Cache.
sfdc.einstein.bots.cache.redis-url=${CACHE_REDIS_URL}
```

### Using Auto Configured Spring Beans

The following beans will be Auto Configured based on information in `application.properties` and can
be Autowired in spring managed code.

#### AuthMechanism

If all oauth properties ( `sfdc.einstein.bots.oauth.*` ) are configured in application
properties, `AuthMechanism` will be auto configured to use JWTBearerOAuth mechanism. Service owners
can also provide their own Auth Mechanism by
implementing  [AuthMechanism](https://git.soma.salesforce.com/chatbots/module-api-sdk-java/blob/master/src/main/java/com/salesforce/chatbot/sdk/auth/AuthMechanism.java)
interface. If custom implementation is found, it will be auto configured.

#### Cache

If `sfdc.einstein.bots.cache.redis-url` is found in application properties, `Cache`  will be auto
configured to use Redis Cache implementation, otherwise `InMemoryCache` implementation will be
injected. Service owners can also provide their own Cache strategy by
implementing [Cache](https://git.soma.salesforce.com/chatbots/module-api-sdk-java/blob/master/src/main/java/com/salesforce/chatbot/sdk/cache/Cache.java)
interface. If custom implementation is found, it will be auto configured.

#### ChatbotClient

If all required properties are found in application properties,  `ChatbotClient` implementation will
be auto configured out of the box.

#### BotsHealthIndicator

An implementation of Spring's Health Indicator `BotsHealthIndicator` will be auto configured to
check health of chatbot runtime system.

### Example

See [ChatbotController](../channel-connector-example/src/main/java/com/salesforce/einsteinbot/connector/example/EinsteinBotController.java)
for example code and [channel-connector-example](../channel-connector-example) dir fully working
application.

### Observability

#### Metrics Instrumentation

Spring micrometer library is used for instrumention and many foundational metrics are already
collected.

You can find all collected metrics for example application
in  [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics/).

#### Metrics Publishing

Spring micrometer can publish metrics to all popular monitoring systems like New Relic, Prometheus,
Graphite etc.

it is responsibility of the service to configure micrometer with appropriate publishig system. Refer
to [documentation](https://micrometer.io/docs) corresponding to your monitoring system.
See [NewRelicMetricsExportAutoConfiguration](example/src/main/java/com/salesforce/chatbot/connector/example/NewRelicMetricsExportAutoConfiguration.java)
as example code for publishing to New Relic.
