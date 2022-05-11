# Einstein Bot Channel Connector Archetype

A Maven archetype for creating a new bot channel connector application. This will autogenerate the project skeleton code.

## How to Use

### 1. Generate Project using Archetype

Execute the following command, **setting archetypeVersion to the most recently released version** of this library and `package`, `groupId`, `artifactId` to whatever you want your app to be called:

```
mvn archetype:generate -DarchetypeGroupId=com.salesforce.einsteinbot -DarchetypeArtifactId=einstein-bot-channel-connector-archetype -DarchetypeVersion=2.0.1 -Dpackage=com.mycompany.myapp.channel -DgroupId=com.mycompany.myapp -DartifactId=myapp-channel-connector -Dversion=1.0.0-SNAPSHOT
```

You can also use following command to run interactively and provide `groupId`, `artifactId` when it prompts.

```
mvn archetype:generate -DarchetypeGroupId=com.salesforce.einsteinbot -DarchetypeArtifactId=einstein-bot-channel-connector-archetype -DarchetypeVersion=2.0.0
```

### 2. Start your application

Follow README instructions of newly created app to start your application.