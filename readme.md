# Spring Boot Channel Connector for Einstein Bots

This is the parent project of Einstein Bot Channel Connector. This contains following submodules:

* [channel-connector-starter](channel-connector-starter) : Channel Connector Framework is spring boot starter that simplifies building channel connectors for the salesforce einstein bot.
* [channel-connector-archetype](channel-connector-archetype) : A Maven archetype for creating a new bot channel connector application
* [examples](examples) : Example applications built using Channel Connector Starter to demonstrate the usage.

Please see README files in submodules for usage.

## Developer Guide

### Code Style

The project uses the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Format settings definition files for importing into IDEs are available for [Eclipse](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)
and [IntelliJ IDEA](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml).

### Publishing to Maven Central

#### Publish Release Versions
Go to [actions](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/master/.github/workflows/maven-release.yml) tab and select **Maven Release** action. Click *Run workflow* , provide good description and hit **Run workflow** button.

#### Publish Snapshot Versions
Go to [actions](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/master/.github/workflows/maven-publish.yml) tab and select **Maven Publish Snapshot** action. Click *Run workflow* , provide good description and hit **Run workflow** button.

#### Choosing Release Versions

The version of the artifact released is same as version defined in [pom.xml](https://github.com/forcedotcom/einstein-bot-channel-connector/blob/master/pom.xml#L21) with `-SNAPSHOT` removed.
After the release pom.xml will be automatically updated with next patch snapshot version.

For eg. if `pom.xml` has version `2.0.1-SNAPSHOT` , then version `2.0.1` will be released and the pom.xml will be updated with version 2.0.2-SNAPSHOT

If you want to release a minor or major version, just update version in pom.xml, For eg, if you want to release `3.0.0` , then set version as  `3.0.0-SNAPSHOT` in pom.xml.

#### Version Numbering

The **minor** and **patch** versions will be incremented for minor features and bug fixes.

The **major** version will be incremented for
* Major changes made to Channel Connector OR
* SDK dependency is upgraded to new version to support new Runtime API version.

Here is the Channel connector version and corresponding Runtime API version supported by it.

| Channel connector Version | Supported Runtime API
| --------------------------| --------------------------------------
| 1.x.x                     | /v4.0.0
| 2.x.x                     | /v5.0.0

### Branching model to support development of multiple API versions

We will maintain the SDK code for each Runtime API version separately in individual branches.
We will cut off release branch for each Runtime API version.

For eg, Currently, the master branch is used to develop for Runtime API Version 5.0.0.
The [releases/api-4.x](https://github.com/forcedotcom/einstein-bot-channel-connector/tree/releases/api-4.x) branch is used to maintain support for API 4.0.0.

So to make change for Channel Connector 1.x.x that supports API v4.0.0, it should be committed to `releases/api-4.x` branch.
Also, choose `releases/api-4.x` when running Release action workflow.
