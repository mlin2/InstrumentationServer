# InstrumentationServer

Instrumentation REST API backend as part of the project Sentinel https://github.com/lolobosse/DroidForce2.
A lengthy report is given at https://github.com/lolobosse/DroidForce/wiki/All-report.

This project uses RAML to generate model classes and endpoints for the Jersey Jax-RS implementation, a Grizzly server and SQLite as a database management system.

To generate the code [this library](https://github.com/mulesoft/raml-for-jax-rs) is used, however it has some important issues concerning the `Multipart` that were already reported [here](https://github.com/mulesoft/raml-for-jax-rs/issues/105)

The code we generated from the RAML is JAX-RS code which is why [Jersey](https://jersey.java.net/) was chosen as an implementation of JAX-RS. Because an JAX-RS implementation is used as a framework for the backend of our instrumentation service, the instrumentation server can be integrated with other JAX-RS implementations or as a servlet with some modification. This allows the project to grow a lot and be easily extensible.

A RAML 0.8 version of of the RAML file was additionally created to be able to generate the interactive html documentation. The documentation can be found in the InstrumentationServer project root and is called interactive-api-documentation.html. To view it, just open this file with your browser.

Furthermore, a [Grizzly](https://grizzly.java.net/) HTTP server is used because Grizzly is an [non-blocking](https://en.wikipedia.org/wiki/Non-blocking_I/O_%28Java%29) input output operations framework and will therefore scale well with many requests for instrumentation from many people.

The database used for the project is [SQLite](https://www.sqlite.org/whentouse.html) as it is simple, looked like a good fit for the initial requirements of storing a small table with APK binary data and hashes, and can store terabytes of data and seems to still offer good performance. Furthermore, it doesn't need to be configured. It turned out that many single insert statements that are currently used to insert the metadata from the F-Droid repository are slow and can take a few minutes. This can probably be improved by using a single big statement to insert the data or by not using SQLite but a client/server database management system.

Note: The hashes used in the implementation of the server always correspond to the uninstrumented versions of APKs because we try to receive data from already having an APK that is not yet instrumented.

### Why is all of that cool?
 * If we change the RAML, the endpoints and model classes can be generated again. Therefore much less implementation is required for changing the REST API.
 * It integrates well with Maven
 * It's just clean software engineering.
 * Because we want that other devs try our server code, we made its configuration pretty simple and devs just have to follow the steps described.
 * Extending the server or including it in a bigger Java server project will be possible.


### Dependencies
We used this instrumentation server implementation on a debian server. For most of the dependencies below you can use "apt-get install packagename" to install them. 

Install Java http://openjdk.java.net/install/

Install Maven https://maven.apache.org/install.html

Install SQLite https://www.sqlite.org/download.html

Install zipalign http://developer.android.com/tools/help/zipalign.html

Get an android jar http://developer.android.com/sdk/index.html

Generate a [keystore](https://www.digitalocean.com/community/tutorials/java-keytool-essentials-working-with-java-keystores) for signing of APKs and if you want to offer HTTPS support for the certificates as well.

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
ServerUrl: https://your.domain.org


[Security]
# Whether or not to enable HTTPS on the server.
enableHTTPS: true

# The absolute path to the app signing keystore
keyStorePathSecurity: your/path/SecurityKeystore

# The password of the security keystore
storePassSecurity: security_password


[Port]
# The port the server will run on
ServerPort: 8080

# In case the port to the server is forwarded, specify the forwarded port
ForwardedPort: 443


[Directories]
# The directory the files created for instrumentation should be saved in
DataDirectory: your/path/InstrumentationServer/instrumentation-server-jobs

# Should the directory be deleted after instrumentation?
DeleteDataDirectory: true


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
fetchMetadata:  true

# The URL to the xml to fetch the metadata from
metadataXmlURL: https://f-droid.org/repo/index.xml

# Fetch APKs from F-Droid
fetchFdroidApks: true

TimeoutForApkFetchingInMinutes: 1
```
An example .ini file is included in the project root.

The tests for the server additionally need a config file at src/test/java/org/sentinel/instrumentationserver. Agai, an example file is given at this location:

```ini
[URL]
# The URL the server will run on.
ServerUrl: http://localhost


[Port]
# The port the server will run on
ServerPort: 8080

# In case the port to the server is forwarded, specify the forwarded port
ForwardedPort: 8080


[Security]
# Whether or not to enable HTTPS on the server.
# Needs to be turned off for the tests.
enableHTTPS: false

# The absolute path to the app signing keystore
keyStorePathSecurity: your/path/SecurityKeystore

# The password of the security keystore
storePassSecurity: security_password


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


