# InstrumentationServer

The REST API backend project InstrumentationServer is part of a [project](https://github.com/lolobosse/DroidForce) to extend [DroidForce](https://github.com/secure-software-engineering/DroidForce) with an [Android app](https://github.com/lolobosse/Sentinel) (the frontend) and this instrumentation server.
All components of the project as submodules are available at https://github.com/lolobosse/DroidForce.

This instrumentation server implementation uses RAML to generate model classes and endpoints for the Jersey Jax-RS implementation, a Grizzly server and SQLite as a database management system.

### Some information

To generate the code [this library](https://github.com/mulesoft/raml-for-jax-rs) is used, however it has some important issues concerning the `Multipart` that were already reported [here](https://github.com/mulesoft/raml-for-jax-rs/issues/105)

The code we generated from the RAML is JAX-RS code which is why [Jersey](https://jersey.java.net/) was chosen as an implementation of JAX-RS. Because an JAX-RS implementation is used as a framework for the backend of our instrumentation service, the instrumentation server can be integrated with other JAX-RS implementations or as a servlet with some modification. This allows the project to grow a lot and be easily extensible.

A RAML 0.8 version of of the RAML file was additionally created to be able to generate the interactive html documentation. The documentation can be found in the InstrumentationServer project root and is called interactive-api-documentation.html. To view it, just open this file with your browser.

Furthermore, a [Grizzly](https://grizzly.java.net/) HTTP server is used because Grizzly is an [non-blocking](https://en.wikipedia.org/wiki/Non-blocking_I/O_%28Java%29) input output operations framework and will therefore scale well with many requests for instrumentation from many people.

The database used for the project is [SQLite](https://www.sqlite.org/whentouse.html) as it is simple, looked like a good fit for the initial requirements of storing a small table with APK binary data and hashes, and can store terabytes of data and seems to still offer good performance. Furthermore, it doesn't need to be configured. It turned out that many single insert statements that are currently used to insert the metadata from the F-Droid repository are slow and can take a few minutes. This can probably be improved by using a single big statement to insert the data or by not using SQLite but a client/server database management system.

Note: The hashes used in the implementation of the server always correspond to the uninstrumented versions of APKs because we try to receive data from already having an APK that is not yet instrumented.

### Advantages of the approach:
 * If we change the RAML, the endpoints and model classes can be generated again. Therefore much less implementation is required for changing the REST API.
 * It integrates well with Maven
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
The instrumentation server needs a file named config.ini in the project root folder. Please use absolute paths. This is an example of what the config file must include:

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

The tests for the server additionally need a config file at src/test/java/org/sentinel/instrumentationserver. Please use absolute paths. Again, an example file is given at this location:

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
### Limitations
 * Overall robustness: The backend for the service has not been thoroughly tested. Therefore while the basic use cases work with the Sentinel app and the server, other cases might not be handled well by the server. To tackle this issue, more people testing and developing the server would be needed.
 * Currently the server fetches remote repository APKs in one single thread because we also want it to be able to handle instrumentation from the sentinel app at the same time. With a stronger server, it would be easy however to split up the list of links to APKs and let several threads instrument them. This may be done with a thread pool.
 * The database queries and Data Access Objects should be improved to handle more special cases with instrumentation data. [Hibernate](http://hibernate.org/) could be used to handle the database queries better than with Strings and prepared statements. For example, model classes for the database could be generated and queries could be written with methods instead of Strings. SQLite is probably not the best choice for an instrumentation server since it takes a long time (about three to five minutes) for the metadata fetching to be done because single transactions are written to the database file.
 * Currently, only png logos are supported because they get returned on the endpoint with the .png extension. Some logo, out of this reason or another unknown reason, do not show up in the app store.
 * A big limitation is that only one version of an instrumented APK can currently be saved on the server. Subsequent requests for the same APK will not be instrumented and the first instrumentation of an APK corresponding to a hash will be returned. Also, some database accesses make usage of the "HASH" (SHA 512 hash) and "SHA256HASH" (SHA 256 hash) fields. They are therefore set to be UNIQUE. This can be changed by accessing by the ID and APKID fields in the tables APKS and METADATA, respectively, implementing the methods in the data access objects differently and removing the UNIQUE statements.
 * As the wrong resource interfaces were generated for form-data multiparts out of the RAML file, we introduced the workaround.resource package with the fixed resource interfaces.
 * The project both includes a jar of the DroidForce project and also a folder for the Instrumentation-PEPs files. We tried getting DroidForce running with a normal Java invocation however could not get it running. Possibly this is the case because of relative paths in the DroidForce implementation.
 * When trying to use XML model classes to map the metadata from an XML file, Jersey returns request failed for all requests. This is probably the case because JacksonFeature registers both the Json model classes and the XML model classes. Therefore, a manual mapping of the XML metadata was implemented. This may be improved by registering a custom ObjectMapper.

