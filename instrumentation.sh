#!/bin/bash

# This bash script runs the instrumentation because we could not get a call to the DroidForce project
# to run the instrumentation correctly.
# This bash script works because it runs the instrumentation in a process.

clear

echo The instrumentation service starts now

echo Be ready $USER !

cd InstrumentationPEP

pwd

ls

echo timeout ${13} java -jar ../DroidForce.jar -sourceFile $1 -sinkFile $2 -taintWrapper $4 -apkFile $3 -o $5 -j -androidJar $6
timeout ${13} java -jar ../DroidForce.jar -sourceFile $1 -sinkFile $2 -taintWrapper $4 -apkFile $3 -o $5 -j -androidJar $6
if [ $? == 0 ]; then

echo The APK gets now signed
# Sign the APK
jarsigner -verbose -keystore $8 -storepass ${10} -signedjar ${11} $7 $9

echo Previous signatures are now removed from the APK
#TODO is this needed?
#Delete previous signatures
zipalign -v 4 ${11} ${12}

fi