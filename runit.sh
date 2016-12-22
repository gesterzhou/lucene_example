#!/bin/bash

#Usage: ~ instanceType serverPort isToUseLocator
# e.g.  ~ 1 50505 true

export GEMFIRE=/Users/gzhou/git_support/gemfire/open/geode-assembly/build/install/apache-geode
#/User/gzhou/.gradle/caches/modules-2/files-2.1/org.apache.geode
#/User/gzhou/.m2/repository/org/apache/geode/
export PROJ=`pwd`
export THIRDPARTY=/Users/gzhou/git3/3rdparty

java -Xmx2048m -cp $PROJ/build/libs/lucene_example-0.0.1.jar:$GEMFIRE/lib/geode-common-1.1.0-incubating-SNAPSHOT.jar:$GEMFIRE/lib/geode-core-1.1.0-incubating-SNAPSHOT.jar:$GEMFIRE/lib/geode-json-1.1.0-incubating-SNAPSHOT.jar:$GEMFIRE/lib/geode-lucene-1.1.0-incubating-SNAPSHOT.jar:$GEMFIRE/lib/geode-dependencies.jar:$GEMFIRE/lib/gfsh-dependencies.jar:bin examples.Main $1 $2 $3


