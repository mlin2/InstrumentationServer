# InstrumentationServer

Instrumentation REST API backend for Securing Android practical course.

This project uses the jersey Jax-RS implementation, a grizzly server and SQLite as a database management system.

To run it, import as maven project in IntelliJ IDEA. Then run the main method in Main.java




The instrumentation server needs a file named config.ini in the project root folder. This is an example of what the config file must include:

```
# The URL the server will run on
[URL]
ServerUrl: http://your.domain.org

# The port the server will run on
[Port]
ServerPort: 8080
# The port the server can be accessed at from the sentinel app. If no value is set, the server assumes ServerPort to be the #port that is accessable by the sentinel app.
ForwardedPort: 443

[Android Jar]
# Path to android Jar
androidJarPath: your/path/android-sdk-linux/platforms/android-19/android.jar

[Keystore]
keyStorePath: your/path/Keystores/instrumentationKeystore

alias: instrumentationKeystore

storePass: your_password
```
