# InstrumentationServer

Instrumentation REST API backend for Securing Android practical course.

This project uses the jersey Jax-RS implementation, a grizzly server and SQLite as a database management system.

Ok so here is how you run the server:


We used this instrumentation server implementation on a debian server. For most of the dependencies below you can use "apt-get install packagename" to install them. 

Install Java http://openjdk.java.net/install/

Install Maven https://maven.apache.org/install.html

Install SQLite https://www.sqlite.org/download.html

Install zipalign http://developer.android.com/tools/help/zipalign.html

Get an android jar http://developer.android.com/sdk/index.html

Furthermore, the operating system running the instrumentation server has to be able to run bash scripts.

All of the above except the android jar file have to be added to your PATH. Alternatively, you can change the bash script "instrumentation.sh" in the projects root folder and add your individual program paths to all the programs that get executed there.

Now, please run following commands:
```
git clone https://github.com/mlin2/InstrumentationServer.git

cd InstrumentationServer

Create a config.ini:
The instrumentation server needs a file named config.ini in the project root folder. This is an example of what the config file must include:

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

[Android]
# Android Platform Path
PlatformsPath: yourpath/android-sdk-linux/platforms/

Run the following commands from your terminal

This will download all the maven dependencies and execute the tests.
mvn test

This will generate all the model classes and endpoint interfaces defined in InstrumentationServer/raml/iaas.raml
mvn raml:generate

This command will run the server with the configuration specified in the config file.
mvn exec:java
```




