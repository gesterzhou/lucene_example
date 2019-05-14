#!/bin/bash

#Usage: ~ instanceType isToUseLocator
# e.g.  ~ 1 false

export GEODEINSTALL=/Users/gzhou/git12/geode/geode-assembly/build/install/apache-geode/lib
export PROJ=`pwd`

java -Xmx2048m -cp $GEODEINSTALL/geode-dependencies.jar:$PROJ/build/libs/lucene_example-0.0.1.jar examples.Main $1 $2


