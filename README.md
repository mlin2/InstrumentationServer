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

The steps to get the server running are as follows:
```
Run in a terminal
git clone https://github.com/mlin2/InstrumentationServer.git

cd InstrumentationServer

Create a config.ini:
The instrumentation server needs a file named config.ini in the project root folder. This is an example of what the config file must include:
```
```
# The URL the server will run on
[URL]
ServerUrl: http://your.domain.org

# The port the server will run on
[Port]
ServerPort: 8080
# The port the server can be accessed at from the sentinel app. If no value is set, the server assumes ServerPort to be the #port that is accessable by the sentinel app.
ForwardedPort: 443

# The directory the database and files created for instrumentation should be saved.
[Directories]
DataDirectory: e.g. InstrumentationServer/instrumentation-server-jobs
DeleteDataDirectory: true

[Android Jar]
# Path to android Jar
androidJarPath: your/path/android-sdk-linux/platforms/android-19/android.jar

[Keystore]
keyStorePath: your/path/Keystores/instrumentationKeystore
alias: instrumentationKeystore
storePass: your_password

[Fetch]
fetchMetadata:  false
fetchFdroidApks: true
TimeoutForApkFetchingInMinutes: 1
```

```
Run the following commands from your terminal

This will download all the maven dependencies and execute the tests.
mvn test

This will generate all the model classes and endpoint interfaces defined in InstrumentationServer/raml/iaas.raml
mvn raml:generate

This command will run the server with the configuration specified in the config file.
mvn exec:java
```
Limitations:
* As the wrong resource interfaces were generated for form-data multiparts out of the RAML file, we introduced the workaround.resource package with the fixed resource interfaces.
* The project both includes a jar of the DroidForce project and also a folder for the Instrumentation-PEPs files.
* When trying to use XML model classes to map the metadata from an XML file, Jersey only returns request failed for all requests. This is probably the case because JacksonFeature registers both the Json model classes and the XML model classes. Therefore, a manual mapping of the XML metadata was implemented. This may be improved by registering a custom ObjectMapper.

