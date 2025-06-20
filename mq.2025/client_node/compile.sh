#!/bin/sh
  
set -x

cd src
javac -Xlint -cp .:../common.jar -d ../bin mq/*.java apps/*.java
