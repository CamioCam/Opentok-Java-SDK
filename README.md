# OpenTok Java SDK

[![Build Status](https://travis-ci.org/opentok/Opentok-Java-SDK.svg)](https://travis-ci.org/opentok/Opentok-Java-SDK)

The OpenTok Java SDK lets you generate
[sessions](http://tokbox.com/opentok/tutorials/create-session/) and
[tokens](http://tokbox.com/opentok/tutorials/create-token/) for [OpenTok](http://www.tokbox.com/)
applications that run on the JVM. This version contains modifications so that it is able to run on AppEngine.

Simply put, AppEngine doesn't appreciate certain HTTP libraries and as it happens Opentok-SDK-Java uses one of these, com.ning.http.client, to make it's API calls. Instead, this version uses URLFetchService to make calls. It should be noted this SDK does *not* support the use of Archives (I never bothered porting those calls), but this would be trivial to implement.

To build this libarary, simply run `gradle build` and ignore all the failing tests. Your JAR should be in the `/build/libs` folder.


# Installation

## Maven Central (recommended):

The [Maven Central](http://central.sonatype.org/) repository helps manage dependencies for JVM
based projects. It can be used via several build tools, including Maven and Gradle.

### Maven

When you use Maven as your build tool, you can manage dependencies in the `pom.xml` file:

```xml
<dependency>
    <groupId>com.tokbox</groupId>
    <artifactId>opentok-server-sdk</artifactId>
    <version>2.2.2</version>
</dependency>
```

### Gradle

When you use Gradle as your build tool, you can manage dependencies in the `build.gradle` file:

```groovy
dependencies {
  compile group: 'com.tokbox', name: 'opentok-server-sdk', version: '2.2.2'
}
```

## Manually:

Download the jar file for the latest release from the
[Releases](https://github.com/opentok/opentok-java-sdk/releases) page. Include it in the classpath
for your own project by
[using the JDK directly](http://docs.oracle.com/javase/7/docs/technotes/tools/windows/classpath.html)
or in your IDE of choice.

# Usage

## Initializing

Import the required classes in any class where it will be used. Then initialize a `com.opentok.OpenTok`
object with your own API Key and API Secret.

```java
import com.opentok.OpenTok;

// inside a class or method...
int apiKey = 000000; // YOUR API KEY
String apiSecret = "YOUR API SECRET";
OpenTok opentok = new OpenTok(apiKey, apiSecret)
```

## Creating Sessions

To create an OpenTok Session, use the `OpenTok` instance's `createSession(SessionProperties properties)`
method. The `properties` parameter is optional and it is used to specify two things:

* Whether the session uses the OpenTok Media Router
* A location hint for the OpenTok server.

An instance can be initialized using the `com.opentok.SessionProperties.Builder` class.
The `sessionId` property of the returned `com.opentok.Session` instance, which you can read using
the `getSessionId()` method, is useful to get an identifier that can be saved to a persistent store
(such as a database).

```java
import com.opentok.Session;
import com.opentok.SessionProperties;

// A session that attempts to stream media directly between clients:
Session session = opentok.createSession();

// A session that uses the OpenTok Media Router:
Session session = opentok.createSession(new SessionProperties.Builder()
  .mediaMode(MediaMode.ROUTED)
  .build());

// A Session with a location hint:
Session session = opentok.createSession(new SessionProperties.Builder()
  .location("12.34.56.78")
  .build());

// Store this sessionId in the database for later use:
String sessionId = session.getSessionId();
```

## Generating Tokens

Once a Session is created, you can start generating Tokens for clients to use when connecting to it.
You can generate a token either by calling an `com.opentok.OpenTok` instance's
`generateToken(String sessionId, TokenOptions options)` method, or by calling a `com.opentok.Session`
instance's `generateToken(TokenOptions options)` method after creating it. The `options` parameter
is optional and it is used to set the role, expire time, and connection data of the token. An
instance can be initialized using the `TokenOptions.Builder` class.

```java
import com.opentok.TokenOptions;
import com.opentok.Role;

// Generate a token from just a sessionId (fetched from a database)
String token = opentok.generateToken(sessionId);
// Generate a token by calling the method on the Session (returned from createSession)
String token = session.generateToken();

// Set some options in a token
String token = session.generateToken(new TokenOptions.Builder()
  .role(Role.MODERATOR)
  .expireTime((System.currentTimeMillis() / 1000L) + (7 * 24 * 60 * 60)) // in one week
  .data("name=Johnny")
  .build());
```

## Working with Archives

You can start the recording of an OpenTok Session using a `com.opentok.OpenTok` instance's
`startArchive(String sessionId, String name)` method. This will return a `com.opentok.Archive` instance.
The parameter `name` is a optional and used to assign a name for the Archive. Note that you can
only start an Archive on a Session that has clients connected.

```java
import com.opentok.Archive;

// A simple Archive (without a name)
Archive archive = opentok.startArchive(sessionId, null);

// Store this archiveId in the database for later use
String archiveId = archive.getId();
```

You can stop the recording of a started Archive using a `com.opentok.Archive` instance's
`stopArchive(String archiveId)` method.

```java
// Stop an Archive from an archiveId (fetched from database)
Archive archive = opentok.stopArchive(archiveId);
```

To get an `com.opentok.Archive` instance (and all the information about it) from an `archiveId`, use
a `com.opentok.OpenTok` instance's `getArchive(String archiveId)` method.

```java
Archive archive = opentok.getArchive(String archiveId);
```

To delete an Archive, you can call a `com.opentok.OpenTok` instance's `deleteArchive(String archiveId)`
method.

```java
// Delete an Archive from an archiveId (fetched from database)
opentok.deleteArchive(archiveId);
```

You can also get a list of all the Archives you've created (up to 1000) with your API Key. This is
done using a `com.opentok.OpenTok` instance's `listArchives(int offset, int count)` method. You may optionally
paginate the Archives you receive using the offset and count parameters. This will return a
`List<Archive>` type.

```java
// Get a list with the first 1000 archives created by the API Key
List<Archive> archives = opentok.listArchives();

// Get a list of the first 50 archives created by the API Key
List<Archive> archives = opentok.listArchives(0, 50);

// Get a list of the next 50 archives
List<Archive> archives = opentok.listArchives(50, 50);
```

# Samples

There are two sample applications included with the SDK. To get going as fast as possible, clone the whole
repository and follow the Walkthroughs:

*  [HelloWorld](sample/HelloWorld/README.md)
*  [Archiving](sample/Archiving/README.md)

# Documentation

Reference documentation is available at <http://www.tokbox.com/opentok/libraries/server/java/reference/index.html> and in the
docs directory of the SDK.

# Requirements

You need an OpenTok API key and API secret, which you can obtain at <https://dashboard.tokbox.com>.

The OpenTok Java SDK requires JDK 6 or greater to compile. Runtime requires Java SE 6 or greater.
This project is tested on both OpenJDK and Oracle implementations.


# Release Notes

See the [Releases](https://github.com/opentok/opentok-java-sdk/releases) page for details
about each release.

# Important changes since v2.2.0

**Changes in v2.2.1:**

The default setting for the `createSession()` method is to create a session with the media mode set
to relayed. In previous versions of the SDK, the default setting was to use the OpenTok Media Router
(with the media mode set to routed in v2.2.0, or with p2p.preference="disabled" in previous
versions). In a relayed session, clients will attempt to send streams directly between each other
(peer to peer); and if clients cannot connect due to firewall restrictions, the session uses the
OpenTok TURN server to relay audio-video streams.

**Changes in v2.2.0:**

This version of the SDK includes support for working with OpenTok 2.0 archives. (This API does not
work with OpenTok 1.0 archives.)

This version of the SDK includes a number of improvements in the API design. These include a number
of API changes. See the OpenTok 2.2 SDK Reference for details on the new API.

The API_Config class has been removed. Store your OpenTok API key and API secret in code outside of the SDK files.

The `create_session()` method has been renamed `createSession()`. Also, the method has changed to
take one parameter: a SessionProperties object. You now generate a SessionProperties object using a Builder pattern.

The `generate_token()` method has been renamed `generateToken()`. Also, the method has changed to
take two parameters: the session ID and a TokenOptions object.

# Development and Contributing

Interested in contributing? We :heart: pull requests! See the [Development](DEVELOPING.md) and
[Contribution](CONTRIBUTING.md) guidelines.

# Support

See <http://tokbox.com/opentok/support/> for all our support options.

Find a bug? File it on the [Issues](https://github.com/opentok/opentok-java-sdk/issues) page. Hint:
test cases are really helpful!
