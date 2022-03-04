# Einstein Bot Channel Connector Archetype

A Maven archetype for creating a new bot channel connector application. This will autogenerate the project skeleton code.

## How to Use

### 1. Initial Setup 

*This is will not be required once this artifacts are pushed to nexus or maven central*

* Clone this repo.
* Run `mvn install`  

### 2. Generate Project using Archetype

Execute the following command, **setting archetypeVersion to the most recently released version** of this library and package, groupId, artifactId to to whatever you want your app to be called:

```
mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=com.salesforce.einsteinbot -DarchetypeArtifactId=einstein-bot-channel-connector-archetype -DarchetypeVersion=1.0.0-SNAPSHOT -Dpackage=com.mycompany.myapp.channel -DgroupId=com.mycompany.myapp -DartifactId=myapp-channel-connector -Dversion=1.0.0-SNAPSHOT
```

### 3. Start your application

Follow README instructions of newly created app to start your application.