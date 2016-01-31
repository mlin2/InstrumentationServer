# InstrumentationServer

Instrumentation REST API backend as part of the project Sentinel https://github.com/lolobosse/DroidForce2.

This project uses the jersey Jax-RS implementation, a grizzly server and SQLite as a database management system.

### Dependencies
We used this instrumentation server implementation on a debian server. For most of the dependencies below you can use "apt-get install packagename" to install them. 

Install Java http://openjdk.java.net/install/

Install Maven https://maven.apache.org/install.html

Install SQLite https://www.sqlite.org/download.html

Install zipalign http://developer.android.com/tools/help/zipalign.html

Get an android jar http://developer.android.com/sdk/index.html

You already should have it if you are running linux but in case you don't, you also need the timeout program that can be found in gnu coreutils.

Furthermore, the operating system running the instrumentation server has to be able to run bash scripts.

All of the above except the android jar file have to be added to your [PATH] (http://unix.stackexchange.com/questions/26047/how-to-correctly-add-a-path-to-path). Alternatively, you can change the bash script "instrumentation.sh" in the projects root folder and add your individual program paths to all the programs that get executed there.

### Running the server
The steps to get the server running are as follows:
Run in a terminal:
```
git clone https://github.com/mlin2/InstrumentationServer.git

cd InstrumentationServer
```
Create a config.ini:
The instrumentation server needs a file named config.ini in the project root folder. This is an example of what the config file must include:

```ini
[URL]
# The URL the server will run on
ServerUrl: http://your.domain.org


[Port]
# The port the server will run on
ServerPort: 8080

# In case the port to the server is forwarded, specify the forwarded port
ForwardedPort: 8080


[Directories]
# The directory the files created for instrumentation should be saved in
DataDirectory: your/path/InstrumentationServer/instrumentation-server-jobs

# Should the directory be deleted after instrumentation?
DeleteDataDirectory: false


[Android Jar]
# Path to android Jar
androidJarPath: your/path/android-sdk-linux/platforms/android-19/android.jar

[Keystore]
# The absolute path to the keystore
keyStorePath: your/path/Keystores/instrumentationKeystore

# The alias of the keystore
alias: instrumentationKeystore

# The pass of the keystore
storePass: your_password


[Fetch]
# Fetch APK metadata form F-Droid
fetchMetadata:  false

# The URL to the xml to fetch the metadata from
metadataXmlURL: https://f-droid.org/repo/index.xml

# Fetch APKs from F-Droid
fetchFdroidApks: false

TimeoutForApkFetchingInMinutes: 1
```
An example .ini file is included in the project root.

The tests for the server additionally need a config file at src/test/java/org/sentinel/instrumentationserver. Agai, an example file is given at this location:

```ini
[URL]
# The URL the server will run on
ServerUrl: http://localhost


[Port]
# The port the server will run on
ServerPort: 8080

# In case the port to the server is forwarded, specify the forwarded port
ForwardedPort: 8080


[Directories]
# The directory the files created for instrumentation should be saved in
DataDirectory: InstrumentationServer/instrumentation-server-jobs

# Should the directory be deleted after instrumentation? Needs to be set to false in order for the tests to work.
DeleteDataDirectory: false


[Android Jar]
# Path to android Jar
androidJarPath: your/path/platforms/android-19/android.jar


[Keystore]
# The absolute path to the keystore
keyStorePath: your/path/keystores/mykeystore

# The alias of the keystore
alias: keystore-alias

# The pass of the keystore
storePass: keystore-password


[Fetch]
# Fetch APK metadata form F-Droid.
# Should be set to false for the tests as they would need a very long time to complete othwise.
fetchMetadata:  false

# The URL to the xml to fetch the metadata from
metadataXmlURL: https://f-droid.org/repo/index.xml

# Fetch APKs from F-Droid
fetchFdroidApks: false

TimeoutForApkFetchingInMinutes: 1
```

```
Run the following commands from your terminal

This will generate all the model classes and endpoint interfaces defined in InstrumentationServer/raml/iaas.raml
mvn raml:generate

This will download all the maven dependencies and execute the tests.
mvn test

This command will run the server with the configuration specified in the config file "config.ini".
mvn exec:java
```


