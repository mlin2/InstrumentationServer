#!/bin/bash

clear

echo The instrumentation service starts yet

echo Be ready $USER !

cd InstrumentationPEP

pwd

ls

echo java -jar ../bit.jar -sourceFile $1 -sinkFile $2 -taintWrapper $4 -apkFile $3 -o $5 -j -androidJar $6
java -jar ../bit.jar -sourceFile $1 -sinkFile $2 -taintWrapper $4 -apkFile $3 -o $5 -j -androidJar $6
